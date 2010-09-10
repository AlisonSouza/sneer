package spikes.wheel.reactive.impl.mocks;

import static sneer.foundation.environments.Environments.my;

import java.util.Random;

import sneer.bricks.hardware.cpu.threads.Threads;
import sneer.bricks.pulp.reactive.Register;
import sneer.bricks.pulp.reactive.Signal;
import sneer.bricks.pulp.reactive.Signals;
import sneer.foundation.lang.Closure;

public class RandomBoolean {

	private static final Random RANDOM = new Random();
	private Register<Boolean> _register = my(Signals.class).newRegister(false);

	{
		my(Threads.class).startStepping(new Closure() { @Override public void run() {
			sleepAndFlip();
		}});
	}
	
	public Signal<Boolean> output() {
		return _register.output();
	}

	private void sleepAndFlip() {
		my(Threads.class).sleepWithoutInterruptions(RANDOM.nextInt(5000));
		_register.setter().consume(!_register.output().currentValue());
	}
	
}
