package org.learning;

public class SnowflakeIdGenerator {
    private final long machineId;
    private final static long EPOCH = 1672531200000L; // Jan 1, 2023 from this time we have 69 years to generate unique id's

    private final static long MACHINE_ID_BITS = 10L;
    private final static long SEQUENCE_BITS = 12L;

    private final static long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS); // This for setting 12 bits to one i.e max value for 12 bits it makes sure to set other 12-64 bits as zero

    private final static long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    private final static long TIMESTAMP_SHIFT = MACHINE_ID_BITS + SEQUENCE_BITS;

    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public SnowflakeIdGenerator(long machineId) {
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException("Machine ID out of range");
        }
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currentTimestamp = currentTime();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // Sequence exhausted for the current millisecond, wait for the next
                // We hae to perform this as System.currentTimeMillis() has time resolution of 1ms in which there is
                // propability that multiple threads are invoked in same milli second, which might cause overlfow of sequence bit usage
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence = 0L; // Reset for new millisecond
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long currentTimestamp) {
        while (currentTimestamp <= lastTimestamp) {
            currentTimestamp = currentTime();
        }
        return currentTimestamp;
    }

    private long currentTime() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);

        for (int i = 0; i < 5; i++) {
            System.out.println(generator.nextId());
        }
    }
}

