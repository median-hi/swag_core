package at.jku.dke.swag;

import at.jku.dke.swag.md_data.MDData;
import at.jku.dke.swag.md_elements.*;
import at.jku.dke.swag.sparql.MDQuerySparqlGenerator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Test {

    @org.junit.Test
    public void test1(){
        MDGraphAndMap mappedGraph = MDGraphInit.initMDGraphAndMap();
        MDGraph mdGraph = mappedGraph.getGraph();
        MappedMDGraph mdMap = mappedGraph.getMap();

        List<Level> groupBy = List.of(new Level("country"));
        Measure m = new Measure("boxOffice");
        MDQuerySparqlGenerator sparqlGen = new MDQuerySparqlGenerator(mdGraph, mdMap);
        String queryStr = sparqlGen.makeAggregationQuery(mdGraph.getFact(), m, groupBy);
        System.out.println(queryStr);
        Query query = QueryFactory.create(queryStr);
        System.out.println(query.toString());
    }

    @org.junit.Test
    public void test2(){
        MDGraphAndMap mappedGraph = MDGraphInit.initMDGraphAndMap();
        MDGraph mdGraph = mappedGraph.getGraph();
        MappedMDGraph mdMap = mappedGraph.getMap();

        List<Level> groupBy = List.of(new Level("country"), new Level("director"));
        MDQuerySparqlGenerator sparqlGen = new MDQuerySparqlGenerator(mdGraph, mdMap);
        String queryStr = sparqlGen.makeDimensionsQuery(mdGraph.getFact(), groupBy);
        System.out.println(queryStr);
        Query query = QueryFactory.create(queryStr);
        System.out.println(query.toString());
    }

    @org.junit.Test
    public void test3(){

        MDGraphAndMap mappedGraph = MDGraphInit.initMDGraphAndMap();
        MDGraph mdGraph = mappedGraph.getGraph();
        MappedMDGraph mdMap = mappedGraph.getMap();
        MDData data = mappedGraph.getData();

        Map<List<String>, Set<String>> factsAndCoordinates = MDGraphUtils.getFactAndCoordinatesAsSet(
                data, mdGraph,
                List.of(new Level("genre")));

        PrintUtils.printMap(factsAndCoordinates);

    }


}
