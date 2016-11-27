package ee;

public abstract interface IEEPowerNet
{
  public static final boolean pullingPower = false;

  public abstract boolean receiveEnergy(int paramInt, byte paramByte, boolean paramBoolean);

  public abstract boolean sendEnergy(int paramInt, byte paramByte, boolean paramBoolean);

  public abstract boolean passEnergy(int paramInt, byte paramByte, boolean paramBoolean);

  public abstract void sendAllPackets(int paramInt);

  public abstract int relayBonus();
}