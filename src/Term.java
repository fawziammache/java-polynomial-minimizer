import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Term {

	// For term "2.1*x^4*y*z^2" we would have the following member
	// assignments...
	private double _coef; // = 2.1
	private ArrayList<String> _vars; // = ["x", "y", "z"]
	private ArrayList<Integer> _pows; // = [4, 1, 2]

	// Constructor that only takes a coefficient
	public Term(double coef) {
		_coef = coef;
		_vars = new ArrayList<String>();
		_pows = new ArrayList<Integer>();
	}

	// Constructor that parses a String representation of a term
	public Term(String s) throws Exception {

		// Initialize this term
		_coef = 1.0d; // Will multiply any constants by this
		_vars = new ArrayList<String>();
		_pows = new ArrayList<Integer>();

		String[] factors = s.split("\\*");
		for (String factor : factors) {
			factor = factor.trim(); // Get rid of leading and trailing
									// whitespace
			try {
				// If successful, multiplies in a constant (multiple constants
				// in a product allowed)
				_coef *= Double.parseDouble(factor);
			} catch (NumberFormatException e) {
				// If not a coefficient, must be a factor "<var>^<pow>"
				// Must be a variable to a power -- parse the factor and add to
				// list
				int pow = 1; // If no power, defaults to 1
				String[] var_pow = factor.split("\\^");
				String var = var_pow[0];
				if (var_pow.length == 2) {
					try { // Second part must be exponent
						pow = Integer.parseInt(var_pow[1]);
					} catch (NumberFormatException f) {
						throw new Exception("ERROR: could not parse " + factor);
					}
				} else if (var_pow.length > 2)
					throw new Exception("ERROR: could not parse " + factor);

				// Successfully parsed variable and power, add to list
				if (_vars.contains(var))
					throw new Exception("ERROR: " + var + " appears twice in " + s);
				_vars.add(var);
				_pows.add(pow);
			}
		}
	}

	// Returns all variables in this term. They are stored in an
	// ArrayList<String>
	// abd one can construct a TreeSet<String> from an ArrayList<String> (and
	// vice versa)
	// by passing one to the constructor of the other. Familiarize yourself with
	// TreeSet operations (Google "Java TreeSet").
	public TreeSet<String> getAllVars() {
		return new TreeSet<String>(_vars);
	}

	// Returns a String representation of this term (can re-parse into same
	// term)
	public String toString() {
		// Using "+" to append Strings involves a lot of String copies since
		// Strings are
		// immutable. StringBuilder is much more efficient for append.
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%01.3f", _coef));
		for (int i = 0; i < _vars.size(); i++) {
			String var = _vars.get(i);
			int pow = _pows.get(i);
			sb.append("*" + var + (pow == 1 ? "" : "^" + pow));
		}
		return sb.toString();
	}

	// Sets the coefficient class member
	public void setCoef(double coef) {
		_coef = coef;
	}

	// Returns the coefficient class member
	public double getCoef() {
		return _coef;
	}

	// Evaluate this term for the given variable assignments
	public double evaluate(HashMap<String, Double> assignments) throws Exception {

		double term_evaluated = 1;

		for (String term : this._vars) {

			boolean termIsInAssignments;
			int term_pow = 1;

			termIsInAssignments = assignments.containsKey(term);

			if (termIsInAssignments) {
				term_pow = this._pows.get(this._vars.indexOf(term));
				term_evaluated *= Math.pow(assignments.get(term), term_pow);
			} else
				throw new Exception("Not implemented");

		}

		return _coef * term_evaluated;
	}

	// Provide the symbolic form resulting from differentiating this term w.r.t.
	// var
	public Term differentiate(String var) throws Exception {

		// We will make a copy of both ArrayLists and the coefficient to keep track of the values of the original polynomial
		ArrayList<String> varsCopy = new ArrayList<String>();
		ArrayList<Integer> powsCopy = new ArrayList<Integer>();
		double coefCopy = _coef; 

		// Empty string to be used later to return the differentiated term
		String diff = ""; 

		// New Term for the differentiated term
		Term t = new Term(diff);

		// for-each loops to copy the values of _vars and _pows in ArrayLists defined above
		for (String variable : _vars) {
			varsCopy.add(variable);
		}

		for (Integer power : _pows) {
			powsCopy.add(power);
		}

		// Boolean to check if var is one of the variables in the term we are differentiating
		boolean varIsInVars;
		varIsInVars = _vars.contains(var);

		// Set the arrays of the new Term equal to those of the original polynomial
		t._vars = varsCopy;
		t._pows = powsCopy;
		t._coef = coefCopy;

		// Power of the var we want to differentiate in the term.
		// e.g. if var = x and p = x^2*y*z^3 then pow_var = 2;
		int pow_var;

		// If _pows is empty, this means that there are no variables in the term, only a constant.
		// pow_var is set to 0 and the differentiated term is equal to 0.
		if (t._pows.isEmpty() || !varIsInVars) {
			pow_var = 0;
			return new Term("0.00");
		} 
		
		// Else, we get the power of var and differentiate
		else {
			pow_var = t._pows.get(t._vars.indexOf(var)); // gets power of the var we want todifferetiate
		}
		
		// When pow_var = 1, var will not appear in the differentiated term
		if (pow_var == 1) {
			t._pows.remove(t._vars.indexOf(var));
			t._vars.remove(var);
			diff = t.toString();
		}

		// When pow_var > 1, pow_var will decrease by 1, and the term will be multiplied by pow_var
		else if (pow_var > 1) {
			t._coef *= pow_var;
			t._pows.set(t._vars.indexOf(var), t._pows.get(t._vars.indexOf(var)) - 1);
			diff = t.toString();
		}

		return t;
	}

	public static void main(String[] args) throws Exception {
		Term t = new Term("x^2*y*z");
		HashMap<String, Double> x0 = new HashMap<String, Double>();
		x0.put("x", 1.0);
		x0.put("y", 2.0);
		x0.put("z", 3.0);
		System.out.println(t.evaluate(x0));
		System.out.println(t.differentiate("z"));
		System.out.println(t.evaluate(x0));
		System.out.println(t.differentiate("x"));
		System.out.println(t.evaluate(x0));
		System.out.println(t.differentiate("y").evaluate(x0));

	}

}
