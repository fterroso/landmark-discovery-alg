package lda.rtree;

import java.io.Serializable;

class DataStruct implements Serializable
{
    int dimension;
    float[] Data;
    
    DataStruct(int _dimension)
    {
        dimension = _dimension;
        Data = new float[dimension];
    }
}