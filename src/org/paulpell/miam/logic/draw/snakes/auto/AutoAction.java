package org.paulpell.miam.logic.draw.snakes.auto;



public class AutoAction
{
	AutoActionType type_;
	int repeats_;
	public AutoAction (AutoActionType t, int rep)
	{
		type_ = t;
		repeats_ = rep;
	}
	
	@Override
	public Object clone()
	{
		return new AutoAction(type_, repeats_);
	}
}