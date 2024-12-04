package incades.concept;

public class Concept<T extends Comparable<T>> implements Comparable<Concept <T>>, Cloneable{

	private T conceptID;
	
	public Concept(T conceptID) {
		super();
		this.conceptID = conceptID;
	}
	

	public Concept(Concept<T> copiedObject) {
		super();
		this.conceptID = copiedObject.getConceptID();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Concept<T> clone(){//canonical implementation
		try{
			return (Concept<T>)super.clone();
		}catch(CloneNotSupportedException e){
			throw new AssertionError(e);
		}
	}

	public Concept() {
		super();
	}

	public T getConceptID() {
		return conceptID;
	}
	
	public void setConceptID(T conceptID) {
		this.conceptID = conceptID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conceptID == null) ? 0 : conceptID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Concept<?> other = (Concept<?>) obj;
		if (conceptID == null) {
			if (other.conceptID != null)
				return false;
		} else if (!conceptID.equals(other.conceptID))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Concept " + conceptID.toString();
	}
	
	public int compareTo(Concept<T> o) {
		return conceptID.compareTo(o.getConceptID());
	}
}
