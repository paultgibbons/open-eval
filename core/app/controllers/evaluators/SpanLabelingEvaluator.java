package controllers.evaluators;

import edu.illinois.cs.cogcomp.core.datastructures.IntPair;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.Constituent;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.View;
import edu.illinois.cs.cogcomp.core.experiments.EvaluationRecord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpanLabelingEvaluator extends Evaluator {

	public SpanLabelingEvaluator() {
		super();
	}

    private String spanViewName = "";

    public void setSpanViewName(String spanViewName) {
        this.spanViewName = spanViewName;
    }

	public Evaluation evaluate(List<TextAnnotation> correctAnnotations, List<TextAnnotation> solverAnnotations) {
        assert( correctAnnotations.size() != solverAnnotations.size() );

        EvaluationRecord macroEvaluationRecord = new EvaluationRecord();
        List<EvaluationRecord> microEvaluationRecords = new ArrayList<>();

        for( int i = 0; i < correctAnnotations.size(); i++) {
            View correctVu = correctAnnotations.get(i).getView(spanViewName);
            View solverVu = solverAnnotations.get(i).getView(spanViewName);

            Set<String> correctSpans = new HashSet<>();
            for(Constituent cons : correctVu.getConstituents() ) {
                correctSpans.add(cons.getLabel());
            }

            Set<String> solverSpans = new HashSet<>();
            for(Constituent cons : solverVu.getConstituents() ) {
                solverSpans.add(cons.getLabel());
            }

            Set<String> spanIntersection = new HashSet<>(correctSpans);
            spanIntersection.retainAll(solverSpans);

            EvaluationRecord microEvaluationRecord = new EvaluationRecord();
            microEvaluationRecord.incrementCorrect(spanIntersection.size());
            microEvaluationRecord.incrementGold(correctSpans.size());
            microEvaluationRecord.incrementPredicted(solverSpans.size());
            microEvaluationRecords.add(microEvaluationRecord);

            macroEvaluationRecord.incrementCorrect(spanIntersection.size());
            macroEvaluationRecord.incrementGold(correctSpans.size());
            macroEvaluationRecord.incrementPredicted(solverSpans.size());
        }

        return new Evaluation(macroEvaluationRecord, microEvaluationRecords);
	}
}