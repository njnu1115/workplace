package leica.disto.transferBLE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.text.format.Time;

public class BleDistoMeasurementFormat
{
  private static final boolean D = false;
  public static final int FeetInchSpaceInchFract = 1;
  public static final int FeetInchTabInchFract = 3;
  public static final int FeetTabInchNoFract = 2;
  public static final int InchFract = 4;
  private static final double METER2FEET = 3.28083989501312D;
  private static final double METER2INCH = 39.3700787D;
  private static final String TAG = "DistoMeasurement";
  private static final int UNIT_DEG_360 = 2;
  private static final int UNIT_DEG_D5 = 6;
  private static final int UNIT_DEG_PM180 = 1;
  private static final int UNIT_DEG_PM90 = 0;
  private static final int UNIT_FEET = 4;
  private static final int UNIT_FTIN_16TH_APO = 6;
  private static final int UNIT_FTIN_32TH_APO = 5;
  private static final int UNIT_FTIN_4TH_APO = 8;
  private static final int UNIT_FTIN_8TH_APO = 7;
  private static final int UNIT_INCH = 9;
  private static final int UNIT_INCH_16TH = 11;
  private static final int UNIT_INCH_32TH = 10;
  private static final int UNIT_INCH_4TH = 13;
  private static final int UNIT_INCH_8TH = 12;
  private static final int UNIT_IN_FT = 5;
  private static final int UNIT_MANUAL = -1;
  private static final int UNIT_METER_CM = 2;
  private static final int UNIT_METER_MM = 0;
  private static final int UNIT_METER_MM_10TH = 1;
  private static final int UNIT_MM_10TH = 3;
  private static final int UNIT_MM_M = 4;
  private static final int UNIT_PERCENT = 3;
  private static final int UNIT_YARD = 14;
  public static final int u_None = 0;
  public static final int u_cm = 11;
  public static final int u_int_mm = 12;
  public static final int u_m = 3;
  public static final int u_mm = 2;
  public String m_Angle;
  public String m_AngleUnit;
  public String m_Distance;
  public String m_DistanceUnit;
  public Time m_Time;
  public boolean m_bEnter = false;
  public boolean m_bMetric = false;
  public boolean m_bTab = false;
  public double m_dBleAngle;
  public double m_dBleDistance;
  public int m_iBleAngleUnit = -1;
  public int m_iBleDistanceUnit = -1;
  public int m_nExcelFormatDezimals = 0;
  
  public void calcBleAngle()
  {
    if (this.m_dBleAngle == 0.0D) {
      return;
    }
    double d2 = this.m_dBleAngle * 180.0D / 3.141592653589793D;
    double d3 = this.m_dBleAngle;
    this.m_AngleUnit = "°";
    double d1;
    switch (this.m_iBleAngleUnit)
    {
    default: 
      return;
    case 0: 
      if ((d3 > 3.141592653589793D) && (d3 < 3.141592653589793D)) {
        d1 = -90.0D + (d2 - 90.0D);
      }
    case 6: 
    case 2: 
      for (;;)
      {
        this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(Math.round(d1 / 0.05D) * 0.05D) });
        return;
        this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(Math.round(d2 / 0.05D) * 0.05D) });
        return;
        if (d3 < -1.5707963267948966D) {
          d1 = 180.0D + d2 + 180.0D;
        }
        for (;;)
        {
          this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(Math.round(d1 / 0.05D) * 0.05D) });
          return;
          d1 = d2;
          if (d3 < 0.0D) {
            d1 = d2 + 360.0D;
          }
        }
        d1 = d2;
        if (d3 < -1.5707963267948966D)
        {
          d1 = d2;
          if (d3 > -3.141592653589793D) {
            d1 = d2 + 180.0D;
          }
        }
      }
    case 1: 
      this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(Math.round(d2 / 0.05D) * 0.05D) });
      return;
    case 3: 
      this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(Math.tan(d3) * 100.0D) });
      this.m_AngleUnit = "%";
      return;
    case 4: 
      this.m_Angle = String.format("%.1f", new Object[] { Double.valueOf(1000.0D * d3) });
      this.m_AngleUnit = "mm/m";
      return;
    }
    this.m_Angle = String.format("%.2f", new Object[] { Double.valueOf(d3 * 12.0D) });
    this.m_AngleUnit = "in/ft";
  }
  
  public void calcBleDistance(Context paramContext)
  {
    SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(paramContext);
    paramContext = paramContext.getResources();
    this.m_bTab = localSharedPreferences.getBoolean(paramContext.getString(2131230883), false);
    this.m_bEnter = localSharedPreferences.getBoolean(paramContext.getString(2131230882), true);
    boolean bool = localSharedPreferences.getBoolean(paramContext.getString(2131230881), false);
    int i = (int)Double.parseDouble(localSharedPreferences.getString(paramContext.getString(2131230884), "0"));
    int j = 0;
    int k = 0;
    int m = 0;
    label172:
    int i2;
    int i3;
    int i4;
    double d2;
    double d1;
    int i1;
    int n;
    switch (i)
    {
    default: 
      switch ((int)Double.parseDouble(localSharedPreferences.getString(paramContext.getString(2131230885), "0")))
      {
      default: 
        i2 = 0;
        i3 = 0;
        i4 = this.m_iBleDistanceUnit;
        d2 = this.m_dBleDistance;
        if ((i4 >= 100) && (i4 < 1000))
        {
          i2 = 1;
          i4 -= 100;
          if ((i4 == 9) || (i4 == 13) || (i4 == 12) || (i4 == 11) || (i4 == 10) || (i4 == 4) || (i4 == 8) || (i4 == 7) || (i4 == 6) || (i4 == 5))
          {
            i = 4;
            d1 = d2 * 3.28083989501312D;
            i1 = i3;
            n = i2;
            label301:
            this.m_DistanceUnit = "m";
            this.m_nExcelFormatDezimals = 2;
            this.m_bMetric = true;
            switch (i)
            {
            default: 
              this.m_Distance = String.format("%.3f", new Object[] { Double.valueOf(d1) });
              this.m_nExcelFormatDezimals = 3;
            }
          }
        }
        break;
      }
      break;
    }
    for (;;)
    {
      if (m != 0) {
        this.m_nExcelFormatDezimals = 0;
      }
      if (!bool) {
        break label1604;
      }
      if (n != 0) {
        this.m_DistanceUnit += "²";
      }
      if (i1 != 0) {
        this.m_DistanceUnit += "³";
      }
      return;
      j = 0;
      break;
      j = 2;
      break;
      j = 11;
      break;
      j = 3;
      break;
      j = 2;
      m = 1;
      break;
      k = 1;
      break label172;
      k = 2;
      break label172;
      k = 3;
      break label172;
      if (i4 == 3)
      {
        i = 0;
        this.m_bMetric = true;
        d1 = d2;
        n = i2;
        i1 = i3;
        break label301;
      }
      d1 = d2;
      n = i2;
      i1 = i3;
      i = i4;
      if (i4 != 14) {
        break label301;
      }
      d1 = 3.28083989501312D * d2 / 3.0D;
      n = i2;
      i1 = i3;
      i = i4;
      break label301;
      d1 = d2;
      n = i2;
      i1 = i3;
      i = i4;
      if (i4 < 1000) {
        break label301;
      }
      i3 = 1;
      i4 -= 1000;
      if ((i4 == 9) || (i4 == 13) || (i4 == 12) || (i4 == 11) || (i4 == 10) || (i4 == 4) || (i4 == 8) || (i4 == 7) || (i4 == 6) || (i4 == 5))
      {
        i = 4;
        d1 = 3.28083989501312D * d2 * 3.28083989501312D;
        n = i2;
        i1 = i3;
        break label301;
      }
      if (i4 == 3)
      {
        i = 0;
        d1 = d2;
        n = i2;
        i1 = i3;
        break label301;
      }
      d1 = d2;
      n = i2;
      i1 = i3;
      i = i4;
      if (i4 != 14) {
        break label301;
      }
      d1 = 3.28083989501312D * d2 / 3.0D * 3.28083989501312D / 3.0D;
      n = i2;
      i1 = i3;
      i = i4;
      break label301;
      switch (j)
      {
      default: 
        this.m_Distance = String.format("%.3f", new Object[] { Double.valueOf(1.0E-5D + d1) });
        this.m_nExcelFormatDezimals = 3;
        this.m_DistanceUnit = "m";
        break;
      case 2: 
        this.m_Distance = String.format("%.1f", new Object[] { Double.valueOf((1.0E-5D + d1) * 1000.0D) });
        this.m_nExcelFormatDezimals = 1;
        this.m_DistanceUnit = "mm";
        break;
      case 11: 
        this.m_Distance = String.format("%.2f", new Object[] { Double.valueOf((1.0E-5D + d1) * 100.0D) });
        this.m_nExcelFormatDezimals = 2;
        this.m_DistanceUnit = "cm";
        continue;
        switch (j)
        {
        default: 
          this.m_Distance = String.format("%.02f", new Object[] { Double.valueOf(1.0E-5D + d1) });
          this.m_nExcelFormatDezimals = 2;
          this.m_DistanceUnit = "m";
          break;
        case 2: 
          this.m_Distance = String.format("%.01f", new Object[] { Double.valueOf((1.0E-5D + d1) * 1000.0D) });
          this.m_nExcelFormatDezimals = 1;
          this.m_DistanceUnit = "mm";
          break;
        case 11: 
          this.m_Distance = String.format("%.01f", new Object[] { Double.valueOf((1.0E-5D + d1) * 100.0D) });
          this.m_nExcelFormatDezimals = 1;
          this.m_DistanceUnit = "cm";
          continue;
          switch (j)
          {
          default: 
            this.m_Distance = String.format("%.01f", new Object[] { Double.valueOf((1.0E-5D + d1) * 1000.0D) });
            this.m_nExcelFormatDezimals = 1;
            this.m_DistanceUnit = "mm";
            break;
          case 3: 
            this.m_Distance = String.format("%.04f", new Object[] { Double.valueOf(1.0E-5D + d1) });
            this.m_nExcelFormatDezimals = 5;
            this.m_DistanceUnit = "m";
            break;
          case 11: 
            this.m_Distance = String.format("%.02f", new Object[] { Double.valueOf((1.0E-5D + d1) * 10.0D) });
            this.m_nExcelFormatDezimals = 3;
            this.m_DistanceUnit = "cm";
            continue;
            switch (j)
            {
            default: 
              this.m_Distance = String.format("%.04f", new Object[] { Double.valueOf(1.0E-5D + d1) });
              this.m_nExcelFormatDezimals = 4;
              this.m_DistanceUnit = "m";
              break;
            case 2: 
              this.m_Distance = String.format("%.01f", new Object[] { Double.valueOf((1.0E-5D + d1) * 1000.0D) });
              this.m_nExcelFormatDezimals = 1;
              this.m_DistanceUnit = "mm";
              break;
            case 11: 
              this.m_Distance = String.format("%.02f", new Object[] { Double.valueOf((1.0E-5D + d1) * 100.0D) });
              this.m_nExcelFormatDezimals = 2;
              this.m_DistanceUnit = "cm";
              continue;
              this.m_Distance = String.format("%.2f", new Object[] { Double.valueOf(3.28083989501312D * d1) });
              this.m_DistanceUnit = "ft";
              this.m_bMetric = false;
              continue;
              this.m_Distance = String.format("%.2f", new Object[] { Double.valueOf(39.3700787D * d1) });
              this.m_DistanceUnit = "in";
              continue;
              this.m_Distance = String.format("%.3f", new Object[] { Double.valueOf(3.28083989501312D * d1 / 3.0D) });
              this.m_DistanceUnit = "yd";
              this.m_nExcelFormatDezimals = 3;
              continue;
              this.m_DistanceUnit = "";
              setBleFractioned(1000.0D * d1, i, k);
              this.m_bMetric = false;
              continue;
              this.m_DistanceUnit = "";
              setBleFractioned(1000.0D * d1, i, 4);
              this.m_bMetric = false;
            }
            break;
          }
          break;
        }
        break;
      }
    }
    label1604:
    this.m_DistanceUnit = null;
    this.m_AngleUnit = null;
  }
  
  public void setBleFractioned(double paramDouble, int paramInt1, int paramInt2)
  {
    double d = paramDouble * 0.0032808399D;
    long l6 = d;
    d = Math.abs(l6 - d) * 12.0D;
    long l5 = d;
    long l4 = (32.0D * Math.abs(l5 - d) + 0.5D);
    long l3 = 32L;
    label57:
    long l2;
    long l1;
    if ((l4 % 2L != 0L) || (l4 == 0L))
    {
      if (paramInt1 != 8) {
        break label275;
      }
      l2 = l4;
      l1 = l3;
      label89:
      if (l1 > 4L) {
        break label250;
      }
    }
    long l7;
    for (;;)
    {
      if ((l2 % 2L != 0L) || (l2 == 0L))
      {
        l4 = l6;
        l3 = l5;
        if (paramDouble < 0.0D)
        {
          l4 = -l6;
          l3 = -l5;
        }
        l7 = l1;
        l6 = l2;
        l5 = l3;
        if (l1 == 1L)
        {
          l7 = l1;
          l6 = l2;
          l5 = l3;
          if (l2 == 1L)
          {
            l6 = 0L;
            l7 = 0L;
            l5 = l3 + 1L;
          }
        }
      }
      switch (paramInt2)
      {
      default: 
        return;
        l4 = (l4 / 2L + 0.5D);
        l3 /= 2L;
        break label57;
        label250:
        l2 = (l2 / 2L + 0.5D);
        l1 /= 2L;
        break label89;
        label275:
        if (paramInt1 == 7) {
          for (;;)
          {
            l1 = l3;
            l2 = l4;
            if (l3 <= 8L) {
              break;
            }
            l4 = (l4 / 2L + 0.5D);
            l3 /= 2L;
          }
        }
        if (paramInt1 == 6) {
          for (;;)
          {
            l1 = l3;
            l2 = l4;
            if (l3 <= 16L) {
              break;
            }
            l4 = (l4 / 2L + 0.5D);
            l3 /= 2L;
          }
        }
        l1 = l3;
        l2 = l4;
        if (paramInt1 == 5)
        {
          for (;;)
          {
            l1 = l3;
            l2 = l4;
            if (l3 <= 32L) {
              break;
            }
            l4 = (l4 / 2L + 0.5D);
            l3 /= 2L;
          }
          l2 /= 2L;
          l1 /= 2L;
        }
        break;
      }
    }
    this.m_Distance = String.format("%d.%02d %d/%d", new Object[] { Long.valueOf(l4), Long.valueOf(l5), Long.valueOf(l6), Long.valueOf(l7) });
    return;
    this.m_Distance = String.format("%d\t%02d", new Object[] { Long.valueOf(l4), Long.valueOf(l5) });
    return;
    this.m_Distance = String.format("%d.%02d\t%d/%d", new Object[] { Long.valueOf(l4), Long.valueOf(l5), Long.valueOf(l6), Long.valueOf(l7) });
    return;
    this.m_Distance = String.format("%d %d/%d", new Object[] { Long.valueOf(12L * l4 + l5), Long.valueOf(l6), Long.valueOf(l7) });
  }
}


/* Location:              C:\opt64\adt-bundle\Disto\classes-dex2jar.jar!\leica\disto\transferBLE\BleDistoMeasurementFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */