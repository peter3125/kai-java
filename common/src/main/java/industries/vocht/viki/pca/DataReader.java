/*
 * Copyright (c) 2016 by Peter de Vocht
 *
 * All rights reserved. No part of this publication may be reproduced, distributed, or
 * transmitted in any form or by any means, including photocopying, recording, or other
 * electronic or mechanical methods, without the prior written permission of the publisher,
 * except in the case of brief quotations embodied in critical reviews and certain other
 * noncommercial uses permitted by copyright law.
 *
 */

package industries.vocht.viki.pca;

import Jama.Matrix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Reads data matrix from a CSV file
 *
 * @author Mateusz Kobos
 * @author Fredrik Bridell 
 */
public class DataReader {

    /**
     * Read data matrix from a CSV file. The first row (corresponding to the header) is ignored.
     * Some lines may be commented out by a '#' character.
     * 
     * This is a convenience wrapper for {@link #read(BufferedReader br, boolean ignoreLastColumn)}
     *
     * @param inStream         CSV file
     * @param ignoreLastColumn if True, the last column is ignored. This is helpful when the last
     *                         column corresponds to a class of a vector.
     * @return data matrix
     */
    public static Matrix read(InputStream inStream, boolean ignoreLastColumn)
        throws IOException {
        DataInputStream in = new DataInputStream(inStream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        return read(br, ignoreLastColumn);
    }


    /**
     * Read data matrix from a CSV file. The first row (corresponding to the header) is ignored.
     * Some lines may be commented out by a '#' character.
     *
     * @param br               a BufferedReader for the CSV file
     * @param ignoreLastColumn if True, the last column is ignored. This is helpful when the last
     *                         column corresponds to a class of a vector.
     * @return data matrix
     */
    public static Matrix read(BufferedReader br, boolean ignoreLastColumn)
        throws IOException {

        String line;
        int lineNo = 0;
        ArrayList<double[]> vectors = new ArrayList<double[]>();
        while ((line = br.readLine()) != null) {
            lineNo++;
            /** Ignore the header line */
            if (lineNo == 1) {
                continue;
            }
            /** Ignore the comments */
            int commentIndex = line.indexOf('#');
            if (commentIndex != -1) {
                line.substring(0, commentIndex);
            }
            line = line.trim();
            /** Ignore empty lines */
            if (line.length() == 0) {
                continue;
            }
            String[] elems = line.split(",");
            int elemsNo = elems.length;
            if (ignoreLastColumn) {
                elemsNo = elems.length - 1;
            }
            double[] vector = new double[elemsNo];
            for (int i = 0; i < elemsNo; i++) {
                vector[i] = Double.parseDouble(elems[i]);
            }
            vectors.add(vector);
        }

        double[][] vectorsArray = new double[vectors.size()][];
        for (int r = 0; r < vectors.size(); r++) {
            vectorsArray[r] = vectors.get(r);
        }
        Matrix m = new Matrix(vectorsArray);
        return m;
    }
    
}
