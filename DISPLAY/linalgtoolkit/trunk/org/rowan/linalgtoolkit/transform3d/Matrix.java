package org.rowan.linalgtoolkit.transform3d;

import org.rowan.linalgtoolkit.Vector3D;

/**
 * The <code>Matrix</code> represents a 4x4 affine transformation matrix. 
 * <p>
 * Matrix data is stored as a 2-dimensional array. The <code>toArray</code> methods 
 * provide a means of accessing this array, as well as a 1-dimensional form in 
 * either column-major or row-major order.
 * 
 * @author Spence DiNicolantonio
 * @version 1.0
 * @since 1.0
 */
public class Matrix {
    
    
    /*********************************************
     * MARK: Constants
     *********************************************/
    
    /** A multidimensional array defining the identity matrix. */
    public static final double[][] IDENTITY_ARRAY = {
        {1, 0, 0, 0}, 
        {0, 1, 0, 0}, 
        {0, 0, 1, 0}, 
        {0, 0, 0, 1}
    };
    
    /** The identity matrix. */
    public static final Matrix IDENTITY = new Matrix(IDENTITY_ARRAY);
    
    
    /*********************************************
     * MARK: Fields
     *********************************************/
    
    /** The order in which a <code>Matrix</code> is stored in an array. */
    public enum ArrayOrder {
        ROW_MAJOR,
        COLUMN_MAJOR
    }
    
    /** The data stored by this matrix. */
    private double[][] data;
    
    
    /*********************************************
     * MARK: Constructors
     *********************************************/    
    
    /**
     * Creates a <code>Matrix</code> object from a given multidimensional array 
     * of values.
     * @param data  The data to be stored in the created matrix.
     * @throws      IllegalArgumentException if the given data array is not a
     *              4x4 array.
     */
    public Matrix(double[][] data) {
        // valid data?
        if (data.length != 4 || data[0].length != 4)
            throw new IllegalArgumentException("Invalid matrix data sent to constructor");
        
        // set matrix data
        this.data = data;
        
        // normalize zero values
        normalizeZeros();
    }
    
    /**
     * Creates a <code>Matrix</code> object with a given array of values.
     * @param data  The data to be stored in the created matrix.
     * @param order The order in which the matrix data is stored.
     * @throws      IllegalArgumentException if the given data array is not a 
     *              an array of length 16.
     */
    public Matrix(double[] data, ArrayOrder order) {
        // valid data?
        if (data.length != 16)
            throw new IllegalArgumentException("Invalid matrix data sent to constructor");
        
        // initialize data field
        this.data = new double[4][4];
        
        // set matrix data
        for (int i=0; i<16; i++)
            if (order == Matrix.ArrayOrder.ROW_MAJOR)
                this.data[i/4][i%4] = data[i];
            else
                this.data[i%4][i/4] = data[i];
        
        // normalize zero values
        normalizeZeros();
    }
    
    
    /*********************************************
     * MARK: Accessors
     *********************************************/
    
    /**
     * Returns the value at a given row and column in this matrix.
     * @param row       The row index containing the desired value.
     * @param column    The column index containing the desired value.
     * @return          The value located at the given row and column in this
     *                  matrix.
     * @throws          IndexOutOfBoundsException if the given values for 
     *                  <code>row</code> or <code>column</code> are not in the
     *                  range [0-3].
     */
    public double getValue(int row, int column) {
        // invalid row/column?
        if (row < 0 || row > 3)
            throw new IndexOutOfBoundsException("Row index out of bounds:" + row);
        if (column < 0 || column > 3)
            throw new IndexOutOfBoundsException("Column index out of bounds:" + column);
        
        // return the requested value
        return this.data[row][column];
    }
    
    /**
     * Returns this matrix's data in a multidimensional array.
     * @return  This matrix's data in a multidimensional array.
     */
    public double[][] toArray() {
        return this.data;
    }
    
    /**
     * Return this matrix's data concatenated into an array using a given order.
     * @param order The order in which the data should be stored in the array. 
     *              The value given for this parameter should be <code>ROW_MAJOR</code>
     *              or <code>COLUMN_MAJOR</code>
     * @return      This matrix's data concatenated into an array with the given 
     *              order.
     */
    public double[] toArray(ArrayOrder order) {
        double[] array = new double[16];
        
        // populate array with values in appropriate order
        for (int i=0; i<4; i++)
            for (int j=0; j<4; j++)
                
                if (order == Matrix.ArrayOrder.ROW_MAJOR)
                    array[i*4 + j] = this.data[i][j];
                else
                    array[j*4 + i] = this.data[i][j];
        
        // return the array
        return array;
    }
    
    
    /*********************************************
     * MARK: Queries
     *********************************************/
    
    /**
     * Determines whether this matrix is the identity matrix.
     * @return  <code>true</code> if this matrix is the identity matrix; 
     *          <code>false</code> otherwise.
     */
    public boolean isIdentity() {
        for (int i=0; i<4; i++)
            for (int j=0; j<4; j++)
                
                // diagonal not equal 1.0?
                if (i == j && this.data[i][j] != 1.0)
                    return false;
        
        // non-diagonal not equal 0.0?
                else if (i != j && this.data[i][j] != 0.0)
                    return false;
        
        // if this point is reached all values must be equal to 0.0, except
        // diagonals, which equal 1.0... so return true
        return true;
    }
    
    
    /*********************************************
     * MARK: Mutators
     *********************************************/
    
    /**
     * Sets the value at a given row and column in this matrix to a given value.
     * <p>
     * This method is not recommended for external use, as a user could accidentally
     * create an erroneous transformation matrix.
     * @param row        The row index at which the given value is to be set.
     * @param column    The column index at which the given value is to be set.
     * @param value        The value to be set a the given row and column in this
     *                    matrix.
     * @throws            IndexOutOfBoundsException if the given values for 
     *                    <code>row</code> or <code>column</code> are not in the
     *                    range [0-3].
     */
    public void setValue(int row, int column, double value) {
        // invalid row/column?
        if (row < 0 || row > 3)
            throw new IndexOutOfBoundsException("Row index out of bounds:" + row);
        if (column < 0 || column > 3)
            throw new IndexOutOfBoundsException("Column index out of bounds:" + column);
        
        // set the value
        this.data[row][column] = value;
    }
    
    
    /*********************************************
     * MARK: Arithmetic
     *********************************************/
    
    /**
     * Calculates the matrix inverse of this matrix. If no inverse exists this 
     * method will return null.
     * @return  The matrix inverse of this matrix; <code>null</code> if this matrix
     *          has no inverse.
     */
    public Matrix inverse() {
        double[] m = toArray(Matrix.ArrayOrder.COLUMN_MAJOR);
        double[] values = new double[16];
        
        // calculate inverse values for stage 1
        // yes, this looks ridiculous, but it is correct...
        values[0]   =  m[5]*m[10]*m[15] - m[5]*m[11]*m[14]  - m[9]*m[6]*m[15]   + m[9]*m[7]*m[14]   + m[13]*m[6]*m[11]  - m[13]*m[7]*m[10];
        values[4]   = -m[4]*m[10]*m[15] + m[4]*m[11]*m[14]  + m[8]*m[6]*m[15]   - m[8]*m[7]*m[14]   - m[12]*m[6]*m[11]  + m[12]*m[7]*m[10];
        values[8]   =  m[4]*m[9]*m[15]  - m[4]*m[11]*m[13]  - m[8]*m[5]*m[15]   + m[8]*m[7]*m[13]   + m[12]*m[5]*m[11]  - m[12]*m[7]*m[9];
        values[12]  = -m[4]*m[9]*m[14]  + m[4]*m[10]*m[13]  + m[8]*m[5]*m[14]   - m[8]*m[6]*m[13]   - m[12]*m[5]*m[10]  + m[12]*m[6]*m[9];
        values[1]   = -m[1]*m[10]*m[15] + m[1]*m[11]*m[14]  + m[9]*m[2]*m[15]   - m[9]*m[3]*m[14]   - m[13]*m[2]*m[11]  + m[13]*m[3]*m[10];
        values[5]   =  m[0]*m[10]*m[15] - m[0]*m[11]*m[14]  - m[8]*m[2]*m[15]   + m[8]*m[3]*m[14]   + m[12]*m[2]*m[11]  - m[12]*m[3]*m[10];
        values[9]   = -m[0]*m[9]*m[15]  + m[0]*m[11]*m[13]  + m[8]*m[1]*m[15]   - m[8]*m[3]*m[13]   - m[12]*m[1]*m[11]  + m[12]*m[3]*m[9];
        values[13]  =  m[0]*m[9]*m[14]  - m[0]*m[10]*m[13]  - m[8]*m[1]*m[14]   + m[8]*m[2]*m[13]   + m[12]*m[1]*m[10]  - m[12]*m[2]*m[9];
        values[2]   =  m[1]*m[6]*m[15]  - m[1]*m[7]*m[14]   - m[5]*m[2]*m[15]   + m[5]*m[3]*m[14]   + m[13]*m[2]*m[7]   - m[13]*m[3]*m[6];
        values[6]   = -m[0]*m[6]*m[15]  + m[0]*m[7]*m[14]   + m[4]*m[2]*m[15]   - m[4]*m[3]*m[14]   - m[12]*m[2]*m[7]   + m[12]*m[3]*m[6];
        values[10]  =  m[0]*m[5]*m[15]  - m[0]*m[7]*m[13]   - m[4]*m[1]*m[15]   + m[4]*m[3]*m[13]   + m[12]*m[1]*m[7]   - m[12]*m[3]*m[5];
        values[14]  = -m[0]*m[5]*m[14]  + m[0]*m[6]*m[13]   + m[4]*m[1]*m[14]   - m[4]*m[2]*m[13]   - m[12]*m[1]*m[6]   + m[12]*m[2]*m[5];
        values[3]   = -m[1]*m[6]*m[11]  + m[1]*m[7]*m[10]   + m[5]*m[2]*m[11]   - m[5]*m[3]*m[10]   - m[9]*m[2]*m[7]    + m[9]*m[3]*m[6];
        values[7]   =  m[0]*m[6]*m[11]  - m[0]*m[7]*m[10]   - m[4]*m[2]*m[11]   + m[4]*m[3]*m[10]   + m[8]*m[2]*m[7]    - m[8]*m[3]*m[6];
        values[11]  = -m[0]*m[5]*m[11]  + m[0]*m[7]*m[9]    + m[4]*m[1]*m[11]   - m[4]*m[3]*m[9]    - m[8]*m[1]*m[7]    + m[8]*m[3]*m[5];
        values[15]  =  m[0]*m[5]*m[10]  - m[0]*m[6]*m[9]    - m[4]*m[1]*m[10]   + m[4]*m[2]*m[9]    + m[8]*m[1]*m[6]    - m[8]*m[2]*m[5];
        
        // calculate determinate
        double determinate = (m[0]*values[0]) + (m[1]*values[4]) + (m[2]*values[8]) + (m[3]*values[12]);
        
        // if determinate equals 0, there is no inverse
        if (determinate == 0)
            return null;
        
        // finish inverse value calculation
        determinate = 1.0 / determinate;
        for (int i = 0; i < 16; i++)
            values[i] *= determinate;
        
        // create and return a new Matrix using the calculated values
        return new Matrix(values, Matrix.ArrayOrder.COLUMN_MAJOR);
    }
    
    /**
     * Calculates the matrix transpose of this matrix.
     * @return  The matrix transpose of this matrix.
     */
    public Matrix transpose() {
        // get matrix data in row-major order
        double[] matrix = toArray(Matrix.ArrayOrder.ROW_MAJOR);
        
        // create and return a new matrix in column-major order
        return new Matrix(matrix, Matrix.ArrayOrder.COLUMN_MAJOR);
    }
    
    /**
     * Calculates the sum of this matrix and a given matrix.
     * @param matrix    The matrix to be added to this matrix.
     * @return          The sum of this matrix and the given matrix.
     */
    public Matrix add(Matrix matrix) {
        // get array of values for each matrix
        double m1[] = toArray(Matrix.ArrayOrder.ROW_MAJOR);
        double m2[] = matrix.toArray(Matrix.ArrayOrder.ROW_MAJOR);
        
        // calculate sum values
        double sum[] = new double[16];
        for (int i=0; i<16; i++)
            sum[i] = m1[i] + m2[i];
        
        // create and return a new Matrix using the calculated sum values
        return new Matrix(sum, Matrix.ArrayOrder.ROW_MAJOR);
    }
    
    /**
     * Calculates the difference between this matrix and a given matrix.
     * @param matrix    The matrix to be subtracted from this matrix.
     * @return          The difference between this matrix and the given matrix.
     */
    public Matrix subtract(Matrix matrix) {
        // get array of values for each matrix
        double m1[] = toArray(Matrix.ArrayOrder.ROW_MAJOR);
        double m2[] = matrix.toArray(Matrix.ArrayOrder.ROW_MAJOR);
        
        // calculate difference values
        double difference[] = new double[16];
        for (int i=0; i<16; i++)
            difference[i] = m1[i] - m2[i];
        
        // create and return a new Matrix using the calculated difference values
        return new Matrix(difference, Matrix.ArrayOrder.ROW_MAJOR);
    }
    
    /**
     * Calculates the product of this matrix and a given 3D Vector.
     * @param vector    The Vector to be multiplied by this matrix.
     * @return          The product of this matrix and the given Vector.
     */
    public Vector3D multiply(Vector3D vector) {
        // store matrix data as an array for easy access
        double[] m = toArray(Matrix.ArrayOrder.ROW_MAJOR);
        
		// store vector magnitude
		double magnitude = vector.magnitude();
        
        // store vector components for easy access
        double vectX = vector.getX();
        double vectY = vector.getY();
        double vectZ = vector.getZ();
        
        // calculate component values
        double x = (m[0]  * vectX) + (m[1]  * vectY) + (m[2]  * vectZ) + m[3];
        double y = (m[4]  * vectX) + (m[5]  * vectY) + (m[6]  * vectZ) + m[7];
        double z = (m[8]  * vectX) + (m[9]  * vectY) + (m[10] * vectZ) + m[11];
        double w = (m[12] * vectX) + (m[13] * vectY) + (m[14] * vectZ) + m[15];
        
        // normalize the resulting 4D vector
        double currMag = Math.sqrt(x*x + y*y + z*z + w*w);
        x *= magnitude / currMag;
        y *= magnitude / currMag;
        z *= magnitude / currMag;
        
        // create and return new Vector using the calculate x, y, and z values
        return new Vector3D(x, y, z);
    }
    
    /**
     * Calculates the product of this matrix and a given matrix.
     * @param matrix    The matrix to be multiplied by this matrix.
     * @return          The product of this matrix and the given matrix.
     */
    public Matrix multiply(Matrix matrix) {
        // store matrix data as an array for easy access
        double[] m1 = toArray(Matrix.ArrayOrder.COLUMN_MAJOR);
        double[] m2 = matrix.toArray(Matrix.ArrayOrder.COLUMN_MAJOR);
        
        // calculate product values
        double[] product = new double[16];
        product[0]  = (m1[0]*m2[0])     + (m1[4]*m2[1])  + (m1[8]*m2[2])    + (m1[12]*m2[3]);
        product[4]  = (m1[0]*m2[4])     + (m1[4]*m2[5])  + (m1[8]*m2[6])    + (m1[12]*m2[7]);
        product[8]  = (m1[0]*m2[8])     + (m1[4]*m2[9])  + (m1[8]*m2[10])   + (m1[12]*m2[11]);
        product[12] = (m1[0]*m2[12])    + (m1[4]*m2[13]) + (m1[8]*m2[14])   + (m1[12]*m2[15]);
        product[1]  = (m1[1]*m2[0])     + (m1[5]*m2[1])  + (m1[9]*m2[2])    + (m1[13]*m2[3]);
        product[5]  = (m1[1]*m2[4])     + (m1[5]*m2[5])  + (m1[9]*m2[6])    + (m1[13]*m2[7]);
        product[9]  = (m1[1]*m2[8])     + (m1[5]*m2[9])  + (m1[9]*m2[10])   + (m1[13]*m2[11]);
        product[13] = (m1[1]*m2[12])    + (m1[5]*m2[13]) + (m1[9]*m2[14])   + (m1[13]*m2[15]);
        product[2]  = (m1[2]*m2[0])     + (m1[6]*m2[1])  + (m1[10]*m2[2])   + (m1[14]*m2[3]);
        product[6]  = (m1[2]*m2[4])     + (m1[6]*m2[5])  + (m1[10]*m2[6])   + (m1[14]*m2[7]);
        product[10] = (m1[2]*m2[8])     + (m1[6]*m2[9])  + (m1[10]*m2[10])  + (m1[14]*m2[11]);
        product[14] = (m1[2]*m2[12])    + (m1[6]*m2[13]) + (m1[10]*m2[14])  + (m1[14]*m2[15]);
        product[3]  = (m1[3]*m2[0])     + (m1[7]*m2[1])  + (m1[11]*m2[2])   + (m1[15]*m2[3]);
        product[7]  = (m1[3]*m2[4])     + (m1[7]*m2[5])  + (m1[11]*m2[6])   + (m1[15]*m2[7]);
        product[11] = (m1[3]*m2[8])     + (m1[7]*m2[9])  + (m1[11]*m2[10])  + (m1[15]*m2[11]);
        product[15] = (m1[3]*m2[12])    + (m1[7]*m2[13]) + (m1[11]*m2[14])  + (m1[15]*m2[15]);
        
        // create and return new Matrix using calculated product values
        return new Matrix(product, Matrix.ArrayOrder.COLUMN_MAJOR);
    }
    
    
    /*********************************************
     * MARK: toString
     *********************************************/    
    
    /**
     * Creates a string to describe this matrix. The returned string will be in
     * row-major order.
     * @return  A string that describes this matrix.
     */
    public String toString() {
        
        // create the string
        String string = "[";
        for (int i=0; i<4; i++) {
            if (i != 0)
                string += ", ";
            
            // row
            string += "[";
            for (int j=0; j<4; j++) {
                if (j != 0)
                    string += ", ";
                string += this.data[i][j];
            }
            string += "]";
            
        }
        string += "]";
        
        // return the created string
        return string;
    }
    
    
    /*********************************************
     * MARK: Private
     *********************************************/    
    
    /**
     * Normalizes -0.0 value components in this matrix to 0.0. This method is used
     * by the matrix constructor to maintain aesthetics.
     */
    private void normalizeZeros() {
        // if value is -0.0 set it to 0.0
        for (int i=0; i<4; i++)
            for (int j=0; j<4; j++)
                this.data[i][j] = (this.data[i][j] == 0.0) ? 0.0 : this.data[i][j];
    }
    
}

