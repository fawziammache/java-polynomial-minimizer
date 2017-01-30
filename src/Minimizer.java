import java.util.HashMap;
import java.util.TreeSet;

public class Minimizer {
	
	private double _eps;			// tolerance
	private int _maxIter;			// maximum number of iterations
	private double _stepSize;		// step size alpha
	private HashMap<String,Double> _var2x0;	   // starting point
	private HashMap<String,Double> _var2lastx; // last point found
	private double _lastObjVal;		// last obj fn value found
	private double _lastGradNorm;   // last gradient norm found
	private long _compTime;			// computation time needed
	private int _nIter;				// no. of iterations needed
	
	private long _startTime;

	private HashMap<String,Polynomial> _var2gradp; // cached Polynomials for gradient expressions

	// Default constructor
	public Minimizer() {
		_eps = 0.001;
		_maxIter = 100;
		_stepSize = 0.05;
		_var2x0 = new HashMap<String,Double>();
		_var2lastx = new HashMap<String,Double>();
	}
	
	// Getters
	public double getEps()      { return _eps; }
	public int getMaxIter()     { return _maxIter; }
	public double getStepSize() { return _stepSize; }
	public HashMap<String,Double> getX0() { return _var2x0; }
	public double getLastObjVal()   { return _lastObjVal; }
	public double getLastGradNorm() { return _lastGradNorm; }
	public HashMap<String,Double> getLastPoint() { return _var2lastx; }
	public int getNIter()       { return _nIter; }
	public long getCompTime()   { return System.currentTimeMillis() - getStartTime(); }
	
	public long getStartTime() { return _startTime; }
	
	// Setters
	public void setEps(double e)      { _eps = e; }
	public void setMaxIter(int m)     { _maxIter = m; }
	public void setStepSize(double s) { _stepSize = s ; }
	public void setX0(HashMap<String,Double> x0) { 
		// This has been done for you, but you should be able to produce 
		// this code on your own own.  Familiarize yourself with TreeSet 
		// operations (Google "Java TreeSet").
		_var2x0.clear(); 
		_var2x0.putAll(x0); // Copies over entries from x0 to _var2x0 
	}
	
	public void setStartTime(long t){
		_startTime = t;
	}
	
	// Run the steepest descent algorithm
	public void minimize(Polynomial p) throws Exception {
	
		setStartTime(System.currentTimeMillis());
		
		HashMap<String,Double> copyOfX0 = new HashMap<String,Double>();
		
		for(String var: this._var2x0.keySet())
			copyOfX0.put(var, this._var2x0.get(var));
		
		// Check that starting point and polynomial have same free variables
		TreeSet<String> vars = p.getAllVars();
		if (!_var2x0.keySet().containsAll(vars)) {
			System.out.println("WARNING: Some variables in " + p + " are not assigned in " + _var2x0 + ".\n");
			_var2x0.clear();
			for (String v : vars)
				_var2x0.put(v, 0.0d);
		}

		// Iteration counter initialization
		this._nIter = 0;
		
		if (!vars.containsAll(this._var2x0.keySet())){
		 throw new Exception("null");
		}
		
		
		while (this._nIter < this.getMaxIter()){
			
			HashMap<String, Polynomial> gradient = new HashMap<String, Polynomial>();
			HashMap<String, Double> gradientEvaluated = new HashMap<String, Double>();

			
			// Makes a gradient HashMap for the polynomial
			for (String var : this.getX0().keySet()){			
				gradient.put(var, p.differentiate(var));
			}
			
			// Makes a HashMap for the evaluated gradient
			for (String var : gradient.keySet()){
				gradientEvaluated.put(var, gradient.get(var).evaluate(this.getX0()));				
				
			}
			
			// From the handout, x = x + step size*direction
			this._var2x0 = VectorUtils.sum(this._var2x0, VectorUtils.scalarMult(-this._stepSize, gradientEvaluated));
			
			// Set equal because we will print _var2lastx
			this._var2lastx = this._var2x0;
			
			// Evaluate 
			this._lastObjVal = p.evaluate(_var2lastx);
			
			// Gradient norm
			this._lastGradNorm = VectorUtils.computeL2Norm(gradientEvaluated);
			
			// checks if gradient norm is smaller or equal to eps
			if (this._lastGradNorm <= this.getEps()){
				this._nIter++;
				System.out.format("At iteration %d: %s objective value = %.3f\n", _nIter, VectorUtils.vectorToString(_var2lastx), _lastObjVal);
				
				break;
			}
			
			// Iteration counter
			this._nIter++;
			
			// Prints iteration to screen
			System.out.format("At iteration %d: %s objective value = %.3f\n", _nIter, VectorUtils.vectorToString(_var2lastx), _lastObjVal);
			
		}
		
		this._var2x0 = copyOfX0;
		
	}

	public static void main(String[] args) throws Exception {
		
		// Assign starting point {x=1.0}
		HashMap<String,Double> x0 = new HashMap<String,Double>();
		x0.put("x", 1.0);

		// Initialize polynomial and minimizer
		Polynomial p = new Polynomial("10*x^2 + -40*x + 40");
		Minimizer  m = new Minimizer();		
		m.setX0(x0);

		System.out.println("Polynomial: " + p);

		// Run minimizer and view result at termination
		m.minimize(p);
		System.out.format("At termination: %s objective value = %.3f\n", m._var2lastx, m._lastObjVal);

		// Output of the test case above should read:
		// ===============================================
		// Polynomial: 10.000*x^2 + -40.000*x + 40.000
		// At iteration 1: {x=2.0} objective value = 0.000
		// At iteration 2: {x=2.0} objective value = 0.000
		// At termination: {x=2.0} objective value = 0.000
		// ===============================================
	}
}
