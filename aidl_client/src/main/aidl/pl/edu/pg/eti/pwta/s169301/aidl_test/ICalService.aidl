// ICalService.aidl
package pl.edu.pg.eti.pwta.s169301.aidl_test;
import pl.edu.pg.eti.pwta.s169301.aidl_test.ICalServiceClient;
import pl.edu.pg.eti.pwta.s169301.aidl_test.City;

interface ICalService {
    oneway void solve(in City[] cities, ICalServiceClient serviceClient);
}