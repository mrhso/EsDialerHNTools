package KO.data;

import java.util.Arrays;
import java.util.Objects;

public class Result {
	public Result(String lastError, byte[] bytes) {
		result = bytes;
		Error_Code = lastError;
	}

	public byte[] result;
	public String Error_Code;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(this.result);
		result = prime * result + Objects.hash(Error_Code);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof Result))
			return false;
		Result other = (Result) obj;
		return Objects.equals(Error_Code, other.Error_Code) && Arrays.equals(result, other.result);
	}

	@Override
	public String toString() {
		return String.format("Result [result=%s, Error_Code=%s]", Arrays.toString(result), Error_Code);
	}
}
