package com.tecsolvent.wizspeak.elasticsearch;


import org.apache.log4j.Logger;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.ScriptService;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

public class ESContentIndexer {

	private ESContentIndexer() {}

	private static final String FROM_DATE = "fromDate";
	private static final String TO_DATE = "toDate";
	private static final String PROPERTIES = "properties";

	private static class ESContentIndexerHolder {
		private static final ESContentIndexer INSTANCE = new ESContentIndexer();
	}
	public static ESContentIndexer getInstance() {
		return ESContentIndexerHolder.INSTANCE;
	}

	Logger logger = Logger.getLogger(ESContentIndexer.class);

	public boolean index(String type, String id, JSONObject metaData) {
		IndexRequestBuilder indexRequestBuilder = getIndexRequestBuilder(type,id, metaData);
		IndexResponse indexResponse = indexRequestBuilder.execute().actionGet();
		return indexResponse.getId()!= null;
	}

	public boolean indexMulti(String type, JSONObject metaDataMap) {
		BulkRequestBuilder bulkRequest = ESClient.getInstance().getClient().prepareBulk();
		Iterator keys = metaDataMap.keys();
		while (keys.hasNext()){
			String key = keys.next().toString();
			bulkRequest.add(getIndexRequestBuilder(type, key,metaDataMap.getJSONObject(key)));
		}

		BulkResponse bulkItemResponses = bulkRequest.execute().actionGet();
		for(BulkItemResponse response : bulkItemResponses){
			if (response.isFailed()) {
				//TODO
				return false;
			}
		}
		return true;
	}

	public Map<String, Object> get(String type, String id){
		GetResponse response = ESClient.getInstance().getClient().prepareGet(type, type, id)
				.execute()
				.actionGet();
		Map<String, Object> sourceAsMap = response.getSourceAsMap();
		if(sourceAsMap != null){
			sourceAsMap.put("SourceMapId",id);
		}
		return sourceAsMap;
	}

	public Map<String, Object> getMulti(String type, List<String> id) {
		MultiGetResponse response = ESClient.getInstance().getClient().prepareMultiGet().add(type, type, id)
				.execute()
				.actionGet();
		MultiGetItemResponse[] responses = response.getResponses();
		Map<String,Set<Object>> data = new HashMap<String, Set<Object>>();
		for(MultiGetItemResponse r : responses){
			if(r.getResponse() != null){
				Map<String, Object> sourceAsMap = r.getResponse().getSourceAsMap();
				for(Map.Entry<String, Object> entry : sourceAsMap.entrySet()){
					if(!data.containsKey(entry.getKey())){
						data.put(entry.getKey(), new HashSet<Object>());
					}
					Set<Object> values = data.get(entry.getKey());
					values.add(entry.getValue());
					data.put(entry.getKey(), values);
				}
			}
		}
		Map<String,Object> res = new HashMap<String, Object>();
		res.putAll(data);
		return res;
	}

	public List<String> search(String type, JSONObject filters){
		List<String> ids = new ArrayList<String>();
		SearchRequestBuilder searchRequestBuilder = ESClient.getInstance().getClient().prepareSearch(type);
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		Iterator keys = filters.keys();
		while (keys.hasNext()){
			String key = keys.next().toString();
			Object value = filters.get(key);
			if(value instanceof JSONArray){
				JSONArray val = (JSONArray) value;
				for(int i = 0 ; i< val.length(); i++){
					queryBuilder.should(QueryBuilders.termQuery(key, val.get(i)));
				}
			} else {
				queryBuilder.should(QueryBuilders.termQuery(key, value));
			}
		}
		searchRequestBuilder.setQuery(queryBuilder);

		SearchResponse searchResponse = searchRequestBuilder
				.setFrom(0).setSize(60)
				.execute()
				.actionGet();

		for (SearchHit hit : searchResponse.getHits()) {
			ids.add(hit.getId());
		}
		return ids;
	}


	public List<String> rangeSearch(String type, JSONObject filters) {

		List<String> ids = new ArrayList<String>();
		SearchRequestBuilder searchRequestBuilder = ESClient.getInstance().getClient().prepareSearch(type);
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

		Iterator keys = filters.keys();
		while (keys.hasNext()){
			String key = keys.next().toString();
			JSONObject valObj = filters.getJSONObject(key);

			queryBuilder.should(QueryBuilders.rangeQuery(key).from(valObj.get(FROM_DATE)).to(valObj.get(TO_DATE)));

		}
		searchRequestBuilder.setQuery(queryBuilder);
		SearchResponse searchResponse = searchRequestBuilder
				.setFrom(0).setSize(50000)
				.execute()
				.actionGet();

		for (SearchHit hit : searchResponse.getHits()) {
			ids.add(hit.getId());
		}
		return ids;
	}

	public boolean delete(String type, String id) {
		DeleteResponse response = ESClient.getInstance().getClient().prepareDelete(type, type, id)
				.execute()
				.actionGet();
		return response.isFound();
	}

	public boolean deleteMulti(String type, List<String> ids) {
		BulkRequestBuilder bulkRequest = ESClient.getInstance().getClient().prepareBulk();
		for(String id: ids) {
			bulkRequest.add(ESClient.getInstance().getClient().prepareDelete(type, type,id));
		}
		BulkResponse bulkItemResponses = bulkRequest
				.execute()
				.actionGet();
		for(BulkItemResponse response : bulkItemResponses){
			if (response.isFailed()) {
				//TODO
				return false;
			}
		}
		return true;
	}

	public Map<String,List<String>> getFacets(String type){
		Map<String,List<String>> facetMap = new HashMap<String, List<String>>();
		Set<String> fields = getFields(type);
		SearchRequestBuilder searchRequestBuilder = ESClient.getInstance().getClient().prepareSearch(type);
		for(String field : fields){
			searchRequestBuilder.addFacet(
					FacetBuilders.termsFacet(field)
							.allTerms(true)
							.field(field)
			);

		}
		SearchResponse searchResponse = searchRequestBuilder
				.execute()
				.actionGet();
		if(searchResponse.getFacets() == null){
			return facetMap;
		}
		Map<String,Facet> stringFacetMap = searchResponse.getFacets().facetsAsMap();

		for(Map.Entry<String, Facet> entry : stringFacetMap.entrySet()){
			TermsFacet termsFacet = (TermsFacet) entry.getValue();
			List<String> values = new ArrayList<String>();
			for(TermsFacet.Entry terms : termsFacet){
				values.add(terms.getTerm().toString());
			}
			facetMap.put(entry.getKey(),values);
		}
		return facetMap;
	}

	public Set<String> getFields(String type) {
		ClusterState cs = ESClient.getInstance().getClient()
				.admin()
				.cluster()
				.prepareState()
				.setIndices(type)
				.execute()
				.actionGet()
				.getState();
		IndexMetaData imd = cs.getMetaData().index(type);
		MappingMetaData mdd = imd.mapping(type);
		try{
			if(mdd == null){
				return Collections.EMPTY_SET;
			}

			Map<String, Object> fieldMap = mdd.getSourceAsMap();
			if(fieldMap != null && fieldMap.containsKey(PROPERTIES)){
				Map<String,Object> properties = (Map<String, Object>) fieldMap.get(PROPERTIES);
				return properties.keySet();
			}
		} catch (IOException ignore){
			return Collections.EMPTY_SET;
		}
		return Collections.EMPTY_SET;
	}

	private IndexRequestBuilder getIndexRequestBuilder(String type, String id, JSONObject metaData) {
		return ESClient.getInstance().getClient().prepareIndex(type,type, id)
				.setConsistencyLevel(WriteConsistencyLevel.QUORUM)
				.setSource(metaData.toString());
	}

	public void updateFieldWithUpsert(String type, String id, List<String> actualValue, String param, String field, String script) throws IOException {
		UpdateRequestBuilder updateRequest = ESClient.getInstance().getClient().prepareUpdate(type, type, id);
		XContentBuilder upsertRequest = XContentFactory.jsonBuilder().startObject().field(field).endObject();
		UpdateResponse response = updateRequest
				.setUpsert(upsertRequest)
				.addScriptParam(param, actualValue)
				.setScript(script, ScriptService.ScriptType.INLINE)
				.execute()
				.actionGet();
	}

	public void updateField(String type, String id, String actualValue, String param, String script) throws IOException {
		UpdateRequestBuilder updateRequest = ESClient.getInstance().getClient().prepareUpdate(type, type, id );
		UpdateResponse response = updateRequest
				.addScriptParam(param, actualValue)
				.setScript(script, ScriptService.ScriptType.INLINE)
				.execute()
				.actionGet();
	}
}
