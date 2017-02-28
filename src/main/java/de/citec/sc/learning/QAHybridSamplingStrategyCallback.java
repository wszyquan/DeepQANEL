package de.citec.sc.learning;



import de.citec.sc.learning.QATrainer.EpochCallback;
import sampling.BeamSearchSampler;
import sampling.samplingstrategies.BeamSearchSamplingStrategies;
import variables.AbstractState;

public class QAHybridSamplingStrategyCallback<StateT extends AbstractState<?>> implements EpochCallback {
	private BeamSearchSampler<?, StateT, ?> sampler;
	private int beamSize = 10;

	public QAHybridSamplingStrategyCallback(BeamSearchSampler<?, StateT, ?> sampler, int beamSize) {
		super();
		this.sampler = sampler;
		this.beamSize = beamSize;
	}

	@Override
	public void onStartEpoch(QATrainer caller, int epoch, int numberOfEpochs, int numberOfInstances) {
		if (epoch % 2 == 0) {
			sampler.setTrainSamplingStrategy(BeamSearchSamplingStrategies.greedyBeamSearchSamplingStrategyByModel(beamSize,
					s -> s.getModelScore()));
		} else {
			sampler.setTrainSamplingStrategy(
					BeamSearchSamplingStrategies.greedyBeamSearchSamplingStrategyByObjective(beamSize, s -> s.getObjectiveScore()));
		}
	}

}
