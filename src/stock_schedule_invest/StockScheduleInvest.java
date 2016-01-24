package stock_schedule_invest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

//import org.geonames.*;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class StockScheduleInvest {
    private BigDecimal stockNumber;	
    private BigDecimal totalMoneyInvested; 
    private BigDecimal totalMoney; 

	public StockScheduleInvest() {
        this.stockNumber = new BigDecimal("0");
	    this.totalMoneyInvested = new BigDecimal("0");
        this.totalMoney = new BigDecimal("0");
    }
	
    private boolean validYear() {
        return true; 
    }

	public void test(int fromYear, int toYear, String companySymbol, int dayOfMonth, BigDecimal money) {	
        Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		int num = 0;
        //boolean weekenOrHoliday;
        boolean beforeInvestDay = true;
        BigDecimal todayPrice; 

        // need to implement this method later 
        if (!this.validYear()) 
            System.exit(-1);

        from.add(Calendar.YEAR, fromYear - from.get(Calendar.YEAR)); 
		to.add(Calendar.YEAR, toYear - to.get(Calendar.YEAR)); 
		 
		try {
		    Stock stock = YahooFinance.get(companySymbol);
		    List<HistoricalQuote> stockHistQuotes;
			stockHistQuotes = stock.getHistory(from, to, Interval.DAILY);
            
            todayPrice = stockHistQuotes.get(0).getAdjClose();
            //System.out.println(todayPrice); 
            //System.out.println(stockHistQuotes.get(0).getDate().getTime()); 

            for (HistoricalQuote item : stockHistQuotes) {
		        // is this the bug in yahoofinance api ???? 
                // why Date is one day before the real date ????
                item.getDate().add(Calendar.DATE, 1 );
                
                BigDecimal tempStockNumber = money.divide(item.getAdjClose(), BigDecimal.ROUND_HALF_DOWN);
                
                if (item.getDate().get(Calendar.DAY_OF_MONTH) > dayOfMonth) {
                    beforeInvestDay = true;     
                }
                else if (item.getDate().get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
                	if (money.compareTo(item.getAdjClose()) > 0) {
	                	this.stockNumber = this.stockNumber.add(tempStockNumber); 
	                    this.totalMoneyInvested = this.totalMoneyInvested.add(money);
	                    beforeInvestDay = false;
	                    num += 1;
	                    System.out.println(item.getDate().getTime());
                		
	                    if (money.compareTo(tempStockNumber.multiply(tempStockNumber)) < 1)
	                    	this.stockNumber.subtract(new BigDecimal("1"));
                	}
                	else 
                		System.out.println("No invest on " + item.getDate().getTime());
                }
                else if (item.getDate().get(Calendar.DAY_OF_MONTH) < dayOfMonth && beforeInvestDay == true) {
                	if (money.compareTo(item.getAdjClose()) > 0) {	              		
                		num += 1;
	                	beforeInvestDay = false;
	                	this.stockNumber = this.stockNumber.add(tempStockNumber); 
	                    this.totalMoneyInvested = this.totalMoneyInvested.add(money);
	                    System.out.println(item.getDate().getTime());
	                    
	                    if (money.compareTo(tempStockNumber.multiply(tempStockNumber)) < 1)
	                    	this.stockNumber.subtract(new BigDecimal("1"));
                	}
                	else 
                		System.out.println("No invest on " + item.getDate().getTime());
                }
            }
            this.totalMoney = todayPrice.multiply(this.stockNumber);
            System.out.println(this.totalMoneyInvested);
            System.out.println(this.stockNumber);
            System.out.println(num);
            System.out.println(this.totalMoney);  
            System.out.println(this.totalMoney.divide(this.totalMoneyInvested, BigDecimal.ROUND_HALF_DOWN));
		}
        catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		StockScheduleInvest Test1 = new StockScheduleInvest();
		Test1.test(2008, 2015, "AZO", 3, new BigDecimal("500"));
	}
}
