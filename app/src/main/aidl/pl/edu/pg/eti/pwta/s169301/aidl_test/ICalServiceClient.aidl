// ICalServiceClient.aidl
package pl.edu.pg.eti.pwta.s169301.aidl_test;

// Declare any non-default types here with import statements
import pl.edu.pg.eti.pwta.s169301.aidl_test.City;

interface ICalServiceClient {
   //oneway void result (int r);
   //oneway void result (String s);
   oneway void result(in City[] shortestPath,in int f, in int iterator, in double timeM);
}