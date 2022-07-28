package at.jku.dke.swag.analysis_graphs.operations;

import at.jku.dke.swag.MDElems.Dimension;
import at.jku.dke.swag.MDElems.Level;
import at.jku.dke.swag.analysis_graphs.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RollUpTo extends OperationTypes{

    private static final RollUpTo instance = new RollUpTo(Collections.emptyList());

    public RollUpTo(List<Object> params) {
        super(params);
    }


    public static OperationTypes getInstance() {
        return instance;
    }

    @Override
    public Set<Update> updSet(AnalysisSituation situation, List<Object> params) {
        Set<Update> updates = new HashSet<>();

        Dimension param0 = (Dimension) params.get(0);
        Level param1 = (Level) params.get(1);

        PairOrConstant actualGran = Utils.actual(situation.getGranularities().get(param0));

        if(!actualGran.equals(Constant.unknown)
                && !actualGran.equals(situation.getMdGraph().top(param0))
                && actualGran.equals(param1)
                && actualGran.isPair()){

            Pair newGranPair = ((Pair)situation.getGranularities()
                    .get(param0)).copy();
            newGranPair.setConstant(param1);
            updates.add(
                    new Update(
                            Location.granularityOf(param0), newGranPair));
        }else{
            if(!actualGran.equals(Constant.unknown)
                    && !actualGran.equals(situation.getMdGraph().top(param0))
                    && actualGran.equals(param1)
                    && actualGran.isConstant()){

                Constant newGranPair = param1;
                updates.add(
                        new Update(
                                Location.granularityOf(param0),
                                param1)
                );
            }
        }

        return updates;
    }
}
