package MBT;

import java.util.Random;

import MBT.AlarmClockImpl;
import org.junit.Assert;
import nz.ac.waikato.modeljunit.*;
import nz.ac.waikato.modeljunit.coverage.ActionCoverage;
import nz.ac.waikato.modeljunit.coverage.CoverageMetric;
import nz.ac.waikato.modeljunit.coverage.StateCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionCoverage;
import nz.ac.waikato.modeljunit.coverage.TransitionPairCoverage;

public class test_class implements FsmModel {
	private static Random rnd = new Random();
	private AlarmClockImpl ac = new AlarmClockImpl();
	private State state;
	private boolean alarmOnTime;
	private boolean alarmCancel;
	private boolean alarmSet;

	private enum State {
		NOT_SET, ALARM_SET, ALARM_RINGING
	}

	@Override
	public Object getState() {
		return (String.valueOf(state));
	}

	@Override
	public void reset(boolean arg0) {
		state = State.NOT_SET;
		alarmSet = false;
		alarmCancel = false;
		alarmOnTime = false;
		ac.reset();
	}

	public boolean setIdleGuard() {
		return (state == State.NOT_SET && !getSet(false) && !getTime(false) && !getCancel(false));
	}

	@Action
	public void setIdle() {
		state = State.NOT_SET;
		Assert.assertEquals(String.valueOf(state), ac.Alarm(alarmSet, alarmOnTime, alarmCancel));
	}

	public boolean setAlarmGuard() {
		return (state == State.NOT_SET && getSet(true) && !getTime(false) && !getCancel(false));
	}

	@Action
	public void setAlarm() {
		state = State.ALARM_SET;
		Assert.assertEquals(String.valueOf(state), ac.Alarm(alarmSet, alarmOnTime, alarmCancel));
	}

	public boolean cancelAlarmGuard() {
		return (state == State.ALARM_SET && getSet(true) && !getTime(false) && getCancel(true));
	}

	@Action
	public void cancelAlarm() {
		state = State.NOT_SET;
		Assert.assertEquals(String.valueOf(state), ac.Alarm(alarmSet, alarmOnTime, alarmCancel));
	}

	public boolean alarmOnTimeGuard() {
		return (state == State.ALARM_SET && getSet(true) && getTime(true) && getCancel(true));
	}

	@Action
	public void alarmOnTime() {
		state = State.ALARM_RINGING;
		Assert.assertEquals(String.valueOf(state), ac.Alarm(alarmSet, alarmOnTime, alarmCancel));
	}

	public boolean alarmOffGuard() {
		return (state == State.ALARM_RINGING && getSet(true) && getTime(true) && getCancel(true));
	}

	@Action
	public void alarmOff() {
		state = State.NOT_SET;
		Assert.assertEquals(String.valueOf(state), ac.Alarm(alarmSet, alarmOnTime, alarmCancel));
		this.reset(false);
	}

	public boolean getTime(boolean value) {
		alarmOnTime = value;
		return alarmOnTime;
	}

	public boolean getCancel(boolean value) {
		alarmCancel = value;
		return alarmCancel;
	}

	public boolean getSet(boolean value) {
		alarmSet = value;
		return alarmSet;
	}
	public static void main(String[] args) {
		Tester tester = new GreedyTester(new test_class());
		System.out.println("_____________________________________");
		tester.setRandom(rnd);
		CoverageMetric trCoverage = new TransitionCoverage();
		tester.addListener(trCoverage);
		CoverageMetric actionCoverage = new ActionCoverage();
		tester.addListener(actionCoverage);
		CoverageMetric tpCoverage = new TransitionPairCoverage();
		tester.addListener(tpCoverage);
		CoverageMetric stCoverage=new StateCoverage();
		tester.addListener(stCoverage);
		tester.addListener("verbose");
		int steps = 0;
		while(tpCoverage.getPercentage()<100) {
			tester.generate();
			steps++;
		}
		System.out.println("generated "+steps+"steps");
		tester.printCoverage();
	}
}
