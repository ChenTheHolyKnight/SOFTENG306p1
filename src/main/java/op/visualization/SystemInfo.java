package op.visualization;

import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.tilesfx.Tile;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.TimerTask;

public class SystemInfo extends TimerTask{
    private Tile memoryTile;
    private Runtime runtime = Runtime.getRuntime();
    private Tile cpuTile;

    public SystemInfo(Tile cpuTile,Tile memoryTile){
        this.cpuTile=cpuTile;
        this.memoryTile=memoryTile;
    }

    public long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public double getMemory() {
        OperatingSystemMXBean operatingSystemMXBean=
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        double free=(double)operatingSystemMXBean.getFreePhysicalMemorySize();
        double total=(double) operatingSystemMXBean.getTotalPhysicalMemorySize();
        double result=(total-free)/total*100;
        return result;

    }

    public double getCPU(){
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return operatingSystemMXBean.getProcessCpuLoad()*100;
    }

    @Override
    public void run() {
        try{
        cpuTile.setUnit("%");
        cpuTile.setValue(this.getCPU());
        if(memoryTile.getUnit()!=null)
            memoryTile.setUnit("%");
        memoryTile.setValue(this.getMemory());
        }catch (Exception e){
            //try catch here to ignore concurrency problem. ie: prevent system crashes even though it's not a problem.
        }
    }
}
