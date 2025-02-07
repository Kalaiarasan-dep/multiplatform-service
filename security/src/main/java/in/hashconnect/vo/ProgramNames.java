package in.hashconnect.vo;

import java.util.Arrays;
import java.util.Optional;

public enum ProgramNames {
	OmoCashback("1", "OmoCashback"),
	OmoPricematch("2", "OmoPricematch"),
	Unidays("3", "OmoUnidays"),
	NotFound("4", "NotFound");
	
	private final String programId;
	private final String programName;
	ProgramNames(String programId, String programName) {
		this.programId = programId;
		this.programName = programName;
	}
	
	public static String getNameById(String programId) {
		Optional<ProgramNames> name =  Arrays.stream(ProgramNames.values())
		            .filter(programNames -> programNames.programId.equals(programId) 
		                || programNames.programId.equals(programId))
		            .findFirst();
		if (name.isEmpty()) {
			return ProgramNames.NotFound.programName;
		}
		
		return name.get().programName;
	}
}
