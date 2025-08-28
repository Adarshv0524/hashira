import java.io.*;
import java.util.*;
import org.json.*;

public class Main { 
    
    public static long decodeFromBase(String value, int base) {
        long result = 0;
        long power = 1;
        
        for (int i = value.length() - 1; i >= 0; i--) {
            char digit = Character.toLowerCase(value.charAt(i));  
            int digitValue = (digit >= '0' && digit <= '9') ? 
                             digit - '0' : digit - 'a' + 10;
            if (digitValue >= base || digitValue < 0) {
                throw new IllegalArgumentException("Invalid digit for base");
            }
            result += digitValue * power;
            power *= base;
        }
        return result;
    }
    
    // Lagrange 
    public static double lagrangeInterpolation(int[] x, long[] y, int target) {
        double result = 0;
        int n = x.length;
        
        for (int i = 0; i < n; i++) {
            double term = y[i];
            
            for (int j = 0; j < n; j++) {
                if (i != j) {
                    term *= (double)(target - x[j]) / (x[i] - x[j]);
                }
            }
            result += term;
        }
        return result;
    }
    
    public static void main(String[] args) {
        try {
            // Read JSON 
            File file = new File("org.json");  
            FileReader reader = new FileReader(file);
            
            // Parse JSON
            JSONObject json = new JSONObject(new JSONTokener(reader));
            
            JSONObject keys = json.getJSONObject("keys");
            int n = keys.getInt("n");
            int k = keys.getInt("k");
            
            List<Integer> xPoints = new ArrayList<>();
            List<Long> yPoints = new ArrayList<>();
            
            // Process each point (take only k points for minimum requirement)
            int count = 0;
            Iterator<String> jsonKeys = json.keys();
            while (jsonKeys.hasNext() && count < k) {
                String key = jsonKeys.next();
                if (!key.equals("keys")) {
                    JSONObject point = json.getJSONObject(key);
                    int x = Integer.parseInt(key);
                    int base = point.getInt("base");
                    String value = point.getString("value");
                    
                    long y = decodeFromBase(value, base);
                    
                    xPoints.add(x);
                    yPoints.add(y);
                    count++;
                    
                    System.out.println("Point: (" + x + ", " + y + ")");
                }
            }
            
            // Convert to arrays
            int[] xArray = xPoints.stream().mapToInt(i -> i).toArray();
            long[] yArray = yPoints.stream().mapToLong(i -> i).toArray();
            
            // Find secret (constant term at x=0)
            double secret = lagrangeInterpolation(xArray, yArray, 0);
            
            System.out.println("\n Secret (c): " + Math.round(secret));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
