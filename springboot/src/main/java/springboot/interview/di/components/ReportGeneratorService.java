package springboot.interview.di.components;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.stereotype.Service;

@Service
public abstract class ReportGeneratorService {

    // Spring dynamically overrides this method
    @Lookup
    public abstract ReportTask getNewReportTask();

    public String generate() {
        ReportTask task = getNewReportTask();
        return task.execute();
    }
}
