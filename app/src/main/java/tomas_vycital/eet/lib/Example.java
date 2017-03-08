package tomas_vycital.eet.lib;

import java.net.MalformedURLException;
import java.util.Date;

import tomas_vycital.eet.lib.exception.EETException;
import tomas_vycital.eet.lib.exception.EETServerException;

/**
 * Created by tom on 2017-03-01
 */
public class Example {
    public static void main( String[] args ) throws MalformedURLException {
        EET eet = new EET( true );

        try {
            System.out.println(
                    eet.send( ( new EETReceipt() )
                            .setCelkTrzba( 3411300 )
                            .setCerpZuct( 67900 )
                            .setCestSluz( 546000 )
                            .setDan1( -17239 )
                            .setDan2( -53073 )
                            .setDan3( 97565 )
                            .setDatTrzby( new Date() )
                            .setDicPopl( "CZ1212121218" )
                            .setIdPokl( "/5546/RO24" )
                            .setIdProvoz( "273" )
                            .setPoradCis( "0/6460/ZQ42" )
                            .setPouzitZboz1( 78400 )
                            .setPouzitZboz2( 96700 )
                            .setPouzitZboz3( 18900 )
                            .setRezim( 0 )
                            .setUrcenoCerpZuct( 32400 )
                            .setZaklDan1( -82092 )
                            .setZaklDan2( -353820 )
                            .setZaklDan3( 975646 )
                            .setZaklNepodlDph( 303600 )
                            .setP12( EET.class.getResourceAsStream( "keysPG/EET_CA1_Playground-CZ1212121218.p12" ), "eet".toCharArray() )
                    )
            );
        } catch ( EETServerException e ) {
            System.out.println( String.format( "EETException %s: %s", e.getCode(), e.toString() ) );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
}
