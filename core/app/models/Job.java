package models;

import controllers.Domain;
import edu.illinois.cs.cogcomp.core.datastructures.textannotation.TextAnnotation;
import edu.illinois.cs.cogcomp.core.utilities.SerializationHelper;
import play.libs.ws.WSResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing one job to send to the solver.
 *
 * @author Joshua Camp
 */

public class Job {

	/** Problem domain */
	Domain domain;

	/** Solver object for processing TextAnnotation objects. */
	private LearnerInterface learnerInterface;

	/** List of unprocessed text annotation instances */
	private List<TextAnnotation> unprocessedInstances;

	/** List of `TextAnnotation` instances returned by the solver */
	private List<TextAnnotation> solverInstances;

	private List<Boolean> skip;

	public Job(LearnerInterface learner, List<TextAnnotation> instances) {
		this.learnerInterface = learner;
		this.unprocessedInstances = instances;
		this.solverInstances = new ArrayList<>();
		this.domain = Domain.TOY;
		skip = new ArrayList<>();
	}

	public WSResponse sendAndReceiveRequestFromSolver(TextAnnotation ta) {
		WSResponse response = null;
		String resultJson;
		TextAnnotation processedInstance;
		response = learnerInterface.processRequest(ta);
		try {
			resultJson = response.getBody();
			processedInstance = SerializationHelper.deserializeFromJson(resultJson);
			solverInstances.add(processedInstance);
			skip.add(false);
		} catch (Exception e) {
			System.out.println(e);
			solverInstances.add(null);
			skip.add(true);
		}
		return response;
	}

	/**
	 * Sends all unprocessed instances to the solver and receives the results.
	 */
	public WSResponse sendAndReceiveRequestsFromSolver() {
		this.solverInstances = new ArrayList<>();
		WSResponse response = null;
		String resultJson;
		TextAnnotation processedInstance;
		for (TextAnnotation ta : unprocessedInstances) {
			response = learnerInterface.processRequest(ta);
			try {
				resultJson = response.getBody();
				processedInstance = SerializationHelper.deserializeFromJson(resultJson);
				solverInstances.add(processedInstance);
				skip.add(false);
			} catch (Exception e) {
				System.out.println(e);
				solverInstances.add(null);
				skip.add(true);
			}
		}
		return response;
	}

	public List<TextAnnotation> getUnprocessedInstances() {
		return unprocessedInstances;
	}

	public List<Boolean> getSkip() {
		return this.skip;
	}

	public List<TextAnnotation> getSolverInstances() {
		return this.solverInstances;
	}

	public TextAnnotation getSolverInstance(int i) {
		return getSolverInstances().get(i);
	}
}
