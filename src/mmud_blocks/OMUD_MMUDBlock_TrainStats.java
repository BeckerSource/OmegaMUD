public class OMUD_MMUDBlock_TrainStats extends OMUD_MMUDBlocks.Block{
	public OMUD_MMUDBlock_TrainStats(){
		_arrlCmdText.add(new CmdText("train stats", 11));	
	}

	public int findBlockData(OMUD_IMUDEvents ommme, StringBuilder sbTelnetData, int pos_offset){
		return -1;
	}

	public void resetData(){}
	public void notifyEvents(OMUD_IMUDEvents ommme){}
	public boolean waitForStatline(){return false;}
}
