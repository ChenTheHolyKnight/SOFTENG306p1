package op.visualization;

import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.tilesfx.Tile;
import javafx.application.Platform;

import java.lang.management.ManagementFactory;
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
        Platform.runLater(() -> {
            try{
                cpuTile.setUnit("%");
                cpuTile.setValue(this.getCPU());
                if(memoryTile.getUnit()!=null)
                    memoryTile.setUnit("%");
                memoryTile.setValue(this.getMemory());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }
}
