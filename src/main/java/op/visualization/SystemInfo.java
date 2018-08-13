package op.visualization;

import com.sun.management.OperatingSystemMXBean;
import eu.hansolo.tilesfx.Tile;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.text.NumberFormat;
import java.util.TimerTask;

public class SystemInfo extends TimerTask{
    private Runtime runtime = Runtime.getRuntime();
    private Tile cpuTile;

    public SystemInfo(Tile cpuTile){
        this.cpuTile=cpuTile;
    }

    public long usedMem() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    public double MemInfo() {
        long allocatedMemory = runtime.totalMemory();
        return usedMem()/allocatedMemory*100;

    }

    public double getCPU(){
        OperatingSystemMXBean operatingSystemMXBean =
                (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return operatingSystemMXBean.getProcessCpuLoad()*100;
    }

    @Override
    public void run() {
        cpuTile.setValue(this.getCPU());
        //cpuTile.setValue(this.MemInfo());
        //cpuTile.setValue(20);
    }
}
