import java.util.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ServiceInfo {
    private int serviceId;
    private String serviceType; // predefined types: OVERHAUL, OIL, BRAKE, etc.
    private double serviceCost;
    private LocalDate serviceDate;
    private int estCompletionTime; // in hours

    // constructor
    public ServiceInfo(int serviceId, String serviceType, double serviceCost, LocalDate serviceDate, int estCompletionTime) {
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.serviceCost = serviceCost;
        this.serviceDate = serviceDate;
        this.estCompletionTime = estCompletionTime;
    }

    // getters and setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public double getServiceCost() {
        return serviceCost;
    }

    public void setServiceCost(double serviceCost) {
        this.serviceCost = serviceCost;
    }

    public LocalDate getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public int getEstCompletionTime() {
        return estCompletionTime;
    }

    public void setEstCompletionTime(int estCompletionTime) {
        this.estCompletionTime = estCompletionTime;
    }
}
