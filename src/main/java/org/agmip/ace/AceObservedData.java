package org.agmip.ace;

import java.io.IOException;

public class AceObservedData extends AceComponent {
    private AceRecordCollection timeseries;

    public AceObservedData(byte[] source) throws IOException {
        super(source);
        this.componentType = AceComponentType.ACE_OBSERVED;
    }

    public AceRecordCollection getTimeseries() throws IOException {
        if (this.timeseries == null) {
            this.timeseries = this.getRecords("timeSeries");
        }
        return this.timeseries;
    }
}
