package at.jku.dke.swag.analysis_graphs;

import at.jku.dke.swag.analysis_graphs.basic_elements.BindableSet;
import at.jku.dke.swag.analysis_graphs.basic_elements.PairOrConstant;
import at.jku.dke.swag.analysis_graphs.utils.Utils;
import at.jku.dke.swag.md_elements.Dimension;
import at.jku.dke.swag.md_elements.Level;
import at.jku.dke.swag.md_elements.LevelMember;
import at.jku.dke.swag.md_elements.MDGraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AnalysisSituation implements Copiable {


    BindableSet measures = new BindableSet();
    BindableSet resultFilters = new BindableSet();
    Map<Dimension, BindableSet> dimensionSelection = new HashMap<>();
    Map<Dimension, PairOrConstant> granularities = new HashMap<>();
    Map<Dimension, PairOrConstant> diceLevels = new HashMap<>();
    Map<Dimension, PairOrConstant> diceNodes = new HashMap<>();
    private MDGraph mdGraph;

    public AnalysisSituation(MDGraph mdGraph) {
        this.mdGraph = mdGraph;

        for (Dimension d : mdGraph.getD()) {
            this.getGranularities().put(d, mdGraph.top(d));
            this.getDiceLevels().put(d, mdGraph.top(d));
            this.getDiceNodes().put(d, new LevelMember("all_" + d.getUri()));
            this.getDimensionSelection().put(d, BindableSet.empty());
        }

        AssertValidSituation();

    }

    public AnalysisSituation setResultFilter(BindableSet resultFilters) {
        this.resultFilters = resultFilters;
        AssertValidSituation();
        return this;
    }

    public AnalysisSituation setDimensionSelection(Dimension d, BindableSet conds) {
        dimensionSelection.put(d, conds);
        AssertValidSituation();
        return this;
    }

    public AnalysisSituation setGran(Dimension d, PairOrConstant gran) {
        granularities.put(d, gran);
        AssertValidSituation();
        return this;
    }

    public AnalysisSituation setDiceLevel(Dimension d, PairOrConstant level) {
        diceLevels.put(d, level);
        AssertValidSituation();
        return this;
    }

    public AnalysisSituation setDiceNode(Dimension d, PairOrConstant node) {
        diceNodes.put(d, node);
        AssertValidSituation();
        return this;
    }

    @Override
    public AnalysisSituation copy() {

        AnalysisSituation newSituation = new AnalysisSituation(this.getMdGraph());

        newSituation.setMeasures(measures.copy());
        newSituation.setResultFilter(resultFilters.copy());

        for (Dimension d : mdGraph.getD()) {
            newSituation.dimensionSelection.put(d, this.dimensionSelection.get(d));
            newSituation.granularities.put(d, this.granularities.get(d));
            newSituation.diceLevels.put(d, this.diceLevels.get(d));
            newSituation.diceNodes.put(d, this.diceNodes.get(d));
        }
        AssertValidSituation();
        return newSituation;
    }

    public MDGraph getMdGraph() {
        return mdGraph;
    }

    public void setMdGraph(MDGraph mdGraph) {
        this.mdGraph = mdGraph;
    }

    public BindableSet getMeasures() {
        return measures;
    }

    public AnalysisSituation setMeasures(BindableSet measures) {
        this.measures = measures;
        AssertValidSituation();
        return this;
    }

    public BindableSet getResultFilters() {
        return resultFilters;
    }

    public void setResultFilters(BindableSet resultFilters) {
        this.resultFilters = resultFilters;
        AssertValidSituation();
    }

    public Map<Dimension, BindableSet> getDimensionSelection() {
        return dimensionSelection;
    }

    public void setDimensionSelection(Map<Dimension, BindableSet> dimensionSelection) {
        this.dimensionSelection = dimensionSelection;
        AssertValidSituation();
    }

    public Map<Dimension, PairOrConstant> getGranularities() {
        return granularities;
    }

    public void setGranularities(Map<Dimension, PairOrConstant> granularities) {
        this.granularities = granularities;
        AssertValidSituation();
    }

    public Map<Dimension, PairOrConstant> getDiceLevels() {
        return diceLevels;
    }

    public void setDiceLevels(Map<Dimension, PairOrConstant> diceLevels) {
        this.diceLevels = diceLevels;
        AssertValidSituation();
    }

    public Map<Dimension, PairOrConstant> getDiceNodes() {
        return diceNodes;
    }

    public void setDiceNodes(Map<Dimension, PairOrConstant> diceNodes) {
        this.diceNodes = diceNodes;
        AssertValidSituation();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnalysisSituation that = (AnalysisSituation) o;
        return Objects.equals(measures, that.measures)
                && Objects.equals(resultFilters, that.resultFilters)
                && Objects.equals(dimensionSelection, that.dimensionSelection)
                && Objects.equals(granularities, that.granularities)
                && Objects.equals(diceLevels, that.diceLevels)
                && Objects.equals(diceNodes, that.diceNodes);
    }

    private void AssertValidSituation() {

        measures.throwInValidExceptionIfInvalid();
        resultFilters.throwInValidExceptionIfInvalid();
        dimensionSelection.forEach((k, v) -> v.throwInValidExceptionIfInvalid());
        diceLevels.forEach((k, v) -> {

            Level level = (Level) Utils.actual(v);
            LevelMember member = (LevelMember) Utils.actual(getDiceNodes().get(k));

            if (level.isUnknown()) {
                if (!member.isUnknown()) {
                    throw new RuntimeException("Invalid Dice level / node");
                }
            } else {
                if (!member.isUnknown()) {
                    if (!mdGraph.isMemberOf(member, level)) {
                        throw new RuntimeException("Invalid Dice level / node");
                    }
                }
            }
        });
    }

    @Override
    public int hashCode() {
        return Objects.hash(measures, resultFilters, dimensionSelection, granularities, diceLevels, diceNodes);
    }
}

