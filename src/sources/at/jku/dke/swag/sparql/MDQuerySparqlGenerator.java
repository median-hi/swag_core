package at.jku.dke.swag.sparql;

import at.jku.dke.swag.md_elements.*;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class MDQuerySparqlGenerator {

    private MDGraph mdGraph;
    private MappedMDGraph mappedMDGraph;

    public MDQuerySparqlGenerator(MDGraph mdGraph, MappedMDGraph mappedMDGraph) {
        this.mdGraph = mdGraph;
        this.mappedMDGraph = mappedMDGraph;
    }

    public static Element joinQueryPatterns(Element e1, Element e2) {

        if (e1 == null && e2 == null)
            return null;
        if (e1 == null && e2 != null)
            return e2;
        if (e1 != null && e2 == null)
            return e1;
        if (e2 instanceof Element && e1 instanceof Element) {
            ElementGroup block2 = new ElementGroup();
            block2.addElement(e1);
            block2.addElement(e2);
            return block2;
        }
        return null;
    }

    public static Query querifyString(String queryString){
        return QueryFactory.create(queryString);
    }

    public  List<MDElement> makePath(Level level){

        List<MDElement> path = new LinkedList<>();

        Dimension dim = mdGraph.findFirstDimensionOfLevel(level);
        Set<Hierarchy> hiers = mdGraph.getDH().get(dim);

        for (Hierarchy hier : hiers) {

            if(mdGraph.getHL().get(hier).contains(level)) {

                path.add(mdGraph.getF().stream().findAny().orElseThrow());
                path.add(level);

                if (level.equals(mdGraph.bot(dim))) {
                    return path;
                }

                Level curLevel = mdGraph.bot(dim);

                while (curLevel != null) {
                    Level next = mdGraph.getNextRollUpLevel(curLevel, hier);
                    if (next != null) {
                        path.add(next);
                        if(next.equals(level)){
                            return path;
                        }
                        curLevel = next;
                    } else {
                        break;
                    }
                }
            }
        }
        return null;
    }

    public String makeQuery(List<MDElement> path){
        StringBuilder builder = new StringBuilder();

        String factQuery = mappedMDGraph.get(mdGraph.getF().stream().findFirst().orElseThrow());
        builder.append(factQuery);

        MDElement prevElm = mdGraph.getF().stream().findFirst().orElseThrow();
        for (MDElement elm : path){
            if (!elm.equals(mdGraph.getF().stream().findFirst().orElseThrow())){
                String tmpLink = mappedMDGraph.get(prevElm, elm);
                String tmp = mappedMDGraph.get(elm);
                builder.append(tmpLink);
                builder.append(tmp);
                prevElm = elm;
            }
        }
        return builder.toString();
    }

}
