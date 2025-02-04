/*
 *    El contenido de este fichero está sujeto a la  Licencia Pública openXpertya versión 1.1 (LPO)
 * en tanto en cuanto forme parte íntegra del total del producto denominado:  openXpertya, solución 
 * empresarial global , y siempre según los términos de dicha licencia LPO.
 *    Una copia  íntegra de dicha  licencia está incluida con todas  las fuentes del producto.
 *    Partes del código son CopyRight (c) 2002-2007 de Ingeniería Informática Integrada S.L., otras 
 * partes son  CopyRight (c) 2002-2007 de  Consultoría y  Soporte en  Redes y  Tecnologías  de  la
 * Información S.L.,  otras partes son  adaptadas, ampliadas,  traducidas, revisadas  y/o mejoradas
 * a partir de código original de  terceros, recogidos en el  ADDENDUM  A, sección 3 (A.3) de dicha
 * licencia  LPO,  y si dicho código es extraido como parte del total del producto, estará sujeto a
 * su respectiva licencia original.  
 *     Más información en http://www.openxpertya.org/ayuda/Licencia.html
 */



package org.openXpertya.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.openXpertya.util.CCache;
import org.openXpertya.util.CLogger;
import org.openXpertya.util.DB;
import org.openXpertya.util.Env;
import org.openXpertya.util.Util;

/**
 * Descripción de Clase
 *
 *
 * @version    2.2, 12.10.07
 * @author     Equipo de Desarrollo de openXpertya    
 */

public class MPriceList extends X_M_PriceList {

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_PriceList_ID
     * @param trxName
     *
     * @return
     */

    public static MPriceList get( Properties ctx,int M_PriceList_ID,String trxName ) {
        Integer    key      = new Integer( M_PriceList_ID );
        MPriceList retValue = ( MPriceList )s_cache.get( key );

        if( retValue == null ) {
            retValue = new MPriceList( ctx,M_PriceList_ID,trxName );
            s_cache.put( key,retValue );
        }

        return retValue;
    }    // get

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param IsSOPriceList
     *
     * @return
     */

    public static MPriceList getDefault( Properties ctx,boolean IsSOPriceList ) {
        return getDefault(ctx, IsSOPriceList, false);
    }    // getDefault
    
    public static MPriceList getDefault( Properties ctx,boolean IsSOPriceList, boolean includeOrg ) {
        int        AD_Client_ID = Env.getAD_Client_ID( ctx );
        int orgID = Env.getAD_Org_ID(ctx);
        
        MPriceList retValue     = null;

        // Search for it in cache

        Iterator it = s_cache.values().iterator();

        while( it.hasNext()) {
            retValue = ( MPriceList )it.next();

			if (retValue.isDefault()
					&& (retValue.getAD_Client_ID() == AD_Client_ID)
					&& retValue.isSOPriceList() == IsSOPriceList
					&& (!includeOrg || orgID == retValue.getAD_Org_ID())) {
                return retValue;
            }
        }

        retValue = null;

		String sql = "SELECT * FROM M_PriceList " + "WHERE AD_Client_ID=?"
				+ (includeOrg ? " AND AD_Org_ID = ? " : "")
				+ " AND IsDefault='Y'"
				+ " AND IsSOPriceList='"+(IsSOPriceList?"Y":"N")+"' AND IsActive='Y' "
				+ "ORDER BY M_PriceList_ID";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,null );
            int i = 1;
            pstmt.setInt( i++,AD_Client_ID );
            if(includeOrg){
            	pstmt.setInt( i++,orgID );
            }

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                retValue = new MPriceList( ctx,rs,null );
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            s_log.log( Level.SEVERE,"getDefault",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        // Return value

        if( retValue != null ) {
            Integer key = new Integer( retValue.getM_PriceList_ID());

            s_cache.put( key,retValue );
        }

        return retValue;
    }    // getDefault
    
    public static MPriceList getPriceList(Properties ctx, Integer orgID, boolean IsSOPriceList, String trxName){
    	MPriceList priceList = null;
		String sql = "select * from m_pricelist where ad_client_id = ? "
				+ (Util.isEmpty(orgID, true) ? "" : "and ad_org_id = " + orgID)
				+ " and issopricelist = '" + (IsSOPriceList ? "Y" : "N")
				+ "' and isactive = 'Y' order by isdefault desc limit 1";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = DB.prepareStatement(sql, trxName);
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			if(rs.next()){
				priceList = new MPriceList(ctx, rs, trxName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(rs != null)rs.close();
				if(ps != null)ps.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return priceList;
    }
    
    /**
     * Get price lists of client
     * @param ctx
     * @param trxName
     * @return
     */
    public static List<MPriceList> getOfClient(Properties ctx, String trxName){
    	//script sql
    	String sql = "SELECT * FROM m_pricelist WHERE ad_client_id = ? "; 
    		
    	List<MPriceList> list = new ArrayList<MPriceList>();
    	PreparedStatement ps = null;
    	ResultSet rs = null;
    	
    	try {
			ps = DB.prepareStatement(sql, trxName);
			//set ad_client
			ps.setInt(1, Env.getAD_Client_ID(ctx));
			rs = ps.executeQuery();
			
			while(rs.next()){
				list.add(new MPriceList(ctx,rs,trxName));				
			}
			
		} catch (Exception e) {
			s_log.log( Level.SEVERE,"getOfClient",e );
			e.printStackTrace();
		} finally{
			try {
				ps.close();
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
				s_log.log( Level.SEVERE,"getOfClient",e );
			}
		}
		
		return list;
    }

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_PriceList_ID
     *
     * @return
     */

    public static int getStandardPrecision( Properties ctx,int M_PriceList_ID ) {
        MPriceList pl = MPriceList.get( ctx,M_PriceList_ID,null );

        return pl.getStandardPrecision();
    }    // getStandardPrecision

    /**
     * Descripción de Método
     *
     *
     * @param ctx
     * @param M_PriceList_ID
     *
     * @return
     */

    public static int getPricePrecision( Properties ctx,int M_PriceList_ID ) {
        MPriceList pl = MPriceList.get( ctx,M_PriceList_ID,null );

        return pl.getPricePrecisionInt();
    }    // getPricePrecision

    /** Descripción de Campos */

    private static CLogger s_log = CLogger.getCLogger( MPriceList.class );

    /** Descripción de Campos */

    private static CCache s_cache = new CCache( "M_PriceList",5 );

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param M_PriceList_ID
     * @param trxName
     */

    public MPriceList( Properties ctx,int M_PriceList_ID,String trxName ) {
        super( ctx,M_PriceList_ID,trxName );

        if( M_PriceList_ID == 0 ) {
            setEnforcePriceLimit( false );
            setIsDefault( false );
            setIsSOPriceList( false );
            setIsTaxIncluded( false );
            setPricePrecision( 2 );    // 2

            // setName (null);
            // setC_Currency_ID (0);

        }
    }                                  // MPriceList

    /**
     * Constructor de la clase ...
     *
     *
     * @param ctx
     * @param rs
     * @param trxName
     */

    public MPriceList( Properties ctx,ResultSet rs,String trxName ) {
        super( ctx,rs,trxName );
    }    // MPriceList

    /** Descripción de Campos */

    private MPriceListVersion m_plv = null;

    /** Descripción de Campos */

    private Integer m_precision = null;

    /**
     * Descripción de Método
     *
     *
     * @param valid
     *
     * @return
     */

    public MPriceListVersion getPriceListVersion( Timestamp valid ) {
        return getPriceListVersion(valid, false);
    }    // getPriceListVersion

    public MPriceListVersion getPriceListVersion( Timestamp valid, boolean ignoreCache ) {
        if( valid == null ) {
            valid = new Timestamp( System.currentTimeMillis());
        }

        // Assume there is no later

        if( !ignoreCache && m_plv != null && m_plv.getValidFrom().before( valid )) {
            return m_plv;
        }

        String sql = "SELECT * FROM M_PriceList_Version " + "WHERE M_PriceList_ID=?" + " AND date_trunc('day',ValidFrom) <= date_trunc('day',?::timestamp) AND isactive = 'Y' " + "ORDER BY ValidFrom DESC";
        PreparedStatement pstmt = null;

        try {
            pstmt = DB.prepareStatement( sql,get_TrxName());
            pstmt.setInt( 1,getM_PriceList_ID());
            pstmt.setTimestamp( 2,valid );

            ResultSet rs = pstmt.executeQuery();

            if( rs.next()) {
                m_plv = new MPriceListVersion( getCtx(),rs,get_TrxName());
            }

            rs.close();
            pstmt.close();
            pstmt = null;
        } catch( Exception e ) {
            log.log( Level.SEVERE,"getPriceListVersion",e );
        }

        try {
            if( pstmt != null ) {
                pstmt.close();
            }

            pstmt = null;
        } catch( Exception e ) {
            pstmt = null;
        }

        if( m_plv == null ) {
            log.warning( "getPriceListVersion - None found M_PriceList_ID=" + getM_PriceList_ID() + " - " + valid + " - " + sql );
        } else {
            log.fine( "getPriceListVersion = " + m_plv );
        }

        return m_plv;
    }    // getPriceListVersion
    
    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getStandardPrecision() {
        if( m_precision == null ) {
            MCurrency c = MCurrency.get( getCtx(),getC_Currency_ID());

            m_precision = new Integer( c.getStdPrecision());
        }

        return m_precision.intValue();
    }    // getStandardPrecision

    /**
     * Descripción de Método
     *
     *
     * @param PricePrecision
     */

    public void setPricePrecision( int PricePrecision ) {
        setPricePrecision( new BigDecimal( PricePrecision ));
    }    // setPricePrecision

    /**
     * Descripción de Método
     *
     *
     * @return
     */

    public int getPricePrecisionInt() {
        BigDecimal bd = getPricePrecision();

        if( bd == null ) {
            return -1;
        }

        return bd.intValue();
    }    // getPricePrecisionInt
}    // MPriceList



/*
 *  @(#)MPriceList.java   02.07.07
 * 
 *  Fin del fichero MPriceList.java
 *  
 *  Versión 2.2
 *
 */
