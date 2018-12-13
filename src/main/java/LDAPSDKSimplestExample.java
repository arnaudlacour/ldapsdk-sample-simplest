import com.unboundid.ldap.sdk.*;

public class LDAPSDKSimplestExample
{
    public static void main(final String[] args)
    {
        try
        {
            // Establish a connection to a local directory as an administrative user
            LDAPConnection ldapConnection = new LDAPConnection("localhost", 1389, "cn=directory manager",
                    "Administrative-user-password-here");
            
            // Create a connection pool from the connection established to the directory server with at least 2
            // connections and 4 at most
            LDAPConnectionPool pool = new LDAPConnectionPool(ldapConnection, 2, 4);
            
            String cn = "test-user";
            String dn = "cn=" + cn + ",dc=example,dc=com";
            
            // prepare an entry to add
            Entry entry = new Entry(dn);
            entry.addAttribute("objectClass", "person");
            entry.addAttribute("cn", cn);
            entry.addAttribute("sn", "example");
            
            // Issue the LDAP ADD request to the server
            pool.add(entry);
            
            // fetch the entry using the connection and print it out on standard output
            SearchResultEntry searchedEntry = ldapConnection.getEntry(dn);
            System.out.println();
            for (String line : searchedEntry.toLDIF())
                System.out.println(line);
            
            // Prepare a couple of modifications to the recently added entry
            Modification mod1 = new Modification(ModificationType.ADD, "userPassword", "2FederateM0re");
            Modification mod2 = new Modification(ModificationType.ADD, "description", "edited user");
            ModifyRequest modify = new ModifyRequest(dn, mod1, mod2);
            
            // Issue the LDAP MODIFY request to effect the modifications
            pool.modify(modify);
            
            // fetch the entry using the pool and print it out on standard output
            searchedEntry = pool.getEntry(dn);
            System.out.println();
            for (String line : searchedEntry.toLDIF())
                System.out.println(line);
            
            // Issue an LDAP DELETE request to the server
            pool.delete(dn);
            
        } catch (LDAPException e)
        {
            e.printStackTrace();
        }
    }
}