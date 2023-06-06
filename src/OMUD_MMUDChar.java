import java.util.ArrayList;

public class OMUD_MMUDChar{
	private OMUD_MMUD.DataRoom 				_dataRoom = 	null;
	private OMUD_MMUD.DataExp 				_dataExp = 		null;
	private OMUD_MMUD.DataStats 			_dataStats = 	null;
	private OMUD_MMUD.DataStatline 			_dataStatline = null;
	private OMUD_MMUD.DataInv 				_dataInv = 		null;
	private OMUD_MMUD.DataParty 			_dataParty = 	null;
	private ArrayList<OMUD_MMUD.DataSpell> 	_dataSpells = 	null;
	public OMUD_MMUDChar(){
		_dataRoom = 	new OMUD_MMUD.DataRoom();
		_dataExp = 		new OMUD_MMUD.DataExp();
		_dataStats = 	new OMUD_MMUD.DataStats();
		_dataStatline = new OMUD_MMUD.DataStatline();
		_dataInv = 		new OMUD_MMUD.DataInv();
		_dataParty = 	new OMUD_MMUD.DataParty();
		_dataSpells = 	new ArrayList<OMUD_MMUD.DataSpell>();
	}
}
