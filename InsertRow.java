/*
 * InsertRow.java
 *
 * DBMS Implementation
 * 
 * edited by: name and email
 * partner (if any): name and email
 */

import java.io.*;
import java.util.Arrays;

/**
 * A class that represents a row that will be inserted in a table in a
 * relational database.
 *
 * This class contains the code used to marshall the values of the
 * individual columns to a single key-value pair.
 */
public class InsertRow {
    private Table table;           // the table in which the row will be inserted
    private Object[] columnVals;   // the column values to be inserted
    private RowOutput keyBuffer;   // buffer for the marshalled row's key
    private RowOutput valueBuffer; // buffer for the marshalled row's value
    private int[] offsets;         // offsets for header of marshalled row's value
    
    /** Constants for special offsets **/
    /** The field with this offset has a null value. */
    public static final int IS_NULL = -1;
    
    /** The field with this offset is a primary key. */
    public static final int IS_PKEY = -2;
    
    /**
     * Constructs an InsertRow object for a row containing the specified
     * values that is to be inserted in the specified table.
     *
     * @param  t  the table
     * @param  values  the column values for the row to be inserted
     */
    public InsertRow(Table table, Object[] values) {
        this.table = table;
        this.columnVals = values;
        this.keyBuffer = new RowOutput();
        this.valueBuffer = new RowOutput();
        
        // Note that we need one more offset than value,
        // so that we can store the offset of the end of the record.
        this.offsets = new int[values.length + 1];
    }
    
    /**
     * Takes the collection of values for this InsertRow
     * and marshalls them into a key/value pair.
     * 
     * The method performs the following steps:
     * 1. Computes the offsets for each column value
     * 2. Writes the offset table to the value buffer
     * 3. Writes the primary key value to the key buffer
     * 4. Writes all non-null, non-primary-key values to the value buffer
     * 
     * @throws IOException if an I/O error occurs during marshalling
     * In theory could occur because this method will use 
     * methods like writeInt() that the RowOutput class inherits from 
     * DataOutputStreamIn but in reality, an IOException should not occur in the
     * context of our RowOutput class.
     * 
     */
    public void marshall() throws IOException {

        int pkIndex = table.primaryKeyColumn().getIndex();
        
        this.offsets = computeOffsets(this.columnVals, pkIndex);

        // Write offset table header to value buffer
        for(int i = 0; i<this.offsets.length; i++){
            this.valueBuffer.writeShort(this.offsets[i]);
        }

        // Write each column value to appropriate buffer
        for(int i = 0; i<this.columnVals.length; i++){
            int columnType = table.getColumn(i).getType();

            if (this.offsets[i] == IS_PKEY){
                // Primary key goes to key buffer
                switch (columnType) {
                    case 0: 
                        this.keyBuffer.writeInt(((Integer) columnVals[i]).intValue());
                        break;
                    case 1:
                        this.keyBuffer.writeDouble(((Double) columnVals[i]).doubleValue());
                        break;
                    case 2: 
                        this.keyBuffer.writeBytes((String) columnVals[i]);
                        break;
                    case 3:
                        this.keyBuffer.writeBytes((String) columnVals[i]);
                        break;
                }
            } else if (this.offsets[i] != IS_NULL) {
                // Non-null, non-primary-key values go to value buffer
                switch (columnType) {
                    case 0: 
                        this.valueBuffer.writeInt(((Integer) columnVals[i]).intValue());
                        break;
                    case 1: 
                        this.valueBuffer.writeDouble(((Double) columnVals[i]).doubleValue());
                        break;
                    case 2: 
                        this.valueBuffer.writeBytes((String) columnVals[i]);
                        break;
                    case 3: 
                        this.valueBuffer.writeBytes((String) columnVals[i]);
                        break;
                }
            }
            // Note: NULL values are skipped cause they only appear in offset table
        }         
    }

    /**
     * Helper function that computes the offset values for each column in the row.
     * 
     * The offset array contains one entry for each column, plus one final
     * entry for the end of record offset for each column.
     * 
     * @param  values  the array of column values to be marshalled
     * @param  pkIdx   the index of the primary key column
     * @return an array of offsets, one per column plus one for the end of record
     * @throws IOException if an I/O error occurs while computing offsets
     */
    private int[] computeOffsets(Object[] values, int pkIdx) throws IOException {
        int[] offsetValues = new int[values.length + 1];
        
        int currentOffset = (values.length + 1) * 2;
        
        for (int i = 0; i < values.length; i++) {
            if (i == pkIdx) {
                // Primary key column
                offsetValues[i] = IS_PKEY;
            } else if (values[i] == null) {
                // Null value
                offsetValues[i] = IS_NULL;
            } else {
                // Non-null, non-primary-key column
                offsetValues[i] = currentOffset;
                
                // Add the size of this column's value to get the next offset
                int columnType = table.getColumn(i).getType();
                switch (columnType) {
                    case 0: 
                        currentOffset += 4;
                        break;
                    case 1: 
                        currentOffset += 8;
                        break;
                    case 2: 
                        currentOffset += table.getColumn(i).getLength();
                        break;
                    case 3:
                        currentOffset += ((String) values[i]).length();
                        break;
                }
            }
        }
        
        offsetValues[values.length] = currentOffset;
        
        return offsetValues;
    }
        
    /**
     * Returns the RowOutput used for the key portion of the marshalled row.
     *
     * @return  the key's RowOutput
     */
    public RowOutput getKeyBuffer() {
        return this.keyBuffer;
    }
    
    /**
     * Returns the RowOutput used for the value portion of the marshalled row.
     *
     * @return  the value's RowOutput
     */
    public RowOutput getValueBuffer() {
        return this.valueBuffer;
    }
    
    /**
     * Returns a String representation of this InsertRow object. 
     *
     * @return  a String for this InsertRow
     */
    public String toString() {
        return "offsets: " + Arrays.toString(this.offsets)
             + "\nkey buffer: " + this.keyBuffer
             + "\nvalue buffer: " + this.valueBuffer;
    }
}
