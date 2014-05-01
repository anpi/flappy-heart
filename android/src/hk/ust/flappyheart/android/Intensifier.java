package hk.ust.flappyheart.android;

/*
 * Sets the intensity state of the game based on the heart beat delta time input
 * 
 * XXX: Why is this a singleton? /anpi 2014-05-01
 */
public class Intensifier {

    private static final Intensifier m_instance = new Intensifier();

    // Private constructor prevents instantiation from other classes
    private Intensifier() {
    }

    public static Intensifier getInstance() {
        return m_instance;
    }

    public static enum IntensityMode {
        INCREASING, DECREASING, DISABLED
    }

    public static enum IntensifierMode {
        CALM, CHALLENGE, ZEN, // Not yet designed >> special developer guru mode
    }

    public static IntensifierMode m_eMode = IntensifierMode.CALM;

    public IntensityMode m_eCurrentIntensityMode = IntensityMode.DISABLED;

    // Shortest
    public static double m_dFastestDelta = 0;
    // Longest
    public static double m_dSlowestDelta = 0;
    private double m_dDeltaSum = 0;
    private double m_dDeltaAvg = 0;
    // Maximum number of beats in Calm Mode
    public static int m_iBeatsInCalmMode = 12;
    private int m_iBeatCounter = 0;

    private int m_iSettingCounter = 0;

    public void SetIntensifierMode(IntensifierMode a_IntensifierMode) {
        if (a_IntensifierMode == IntensifierMode.CALM) {
            m_iBeatCounter = 0;
            m_dDeltaSum = 0;
            m_dDeltaAvg = 0;
        } else {

        }

        m_eMode = a_IntensifierMode;
    }

    public void OnPulseDeltaCalculated(double a_dDeltaTime) {
        ++m_iBeatCounter;

        if (a_dDeltaTime > 0) {
            // If CALM mode then collect data to get average delta times
            // So the faster the person beats the faster he will lose time for
            // the CALM mode
            if (m_eMode == IntensifierMode.CALM) {
                m_dDeltaSum += a_dDeltaTime;

                if (m_iBeatCounter > m_iBeatsInCalmMode) {
                    m_dDeltaAvg = m_dDeltaSum / m_iBeatCounter;

                    m_dDeltaSum = 0;
                    m_iBeatCounter = 0;
                    SetIntensifierMode(IntensifierMode.CHALLENGE);
                    // Begin Challenge Timer Flag
                    return;
                }
            }
            // Every time a beat is received in Challenge mode, we need to set
            // the corresponding intensity mode...
            // Exit condition of the Challenge mode is to ( spend an x amount of
            // time ) ***TO IMPLEMENT***
            else if (m_eMode == IntensifierMode.CHALLENGE) {
                if (a_dDeltaTime > m_dDeltaAvg) {
                    if (m_eCurrentIntensityMode != IntensityMode.DECREASING) {
                        m_iSettingCounter = 0;
                        m_eCurrentIntensityMode = IntensityMode.DECREASING;
                    }
                    ++m_iSettingCounter;
                } else if (a_dDeltaTime < m_dDeltaAvg) {
                    if (m_eCurrentIntensityMode != IntensityMode.INCREASING) {
                        m_iSettingCounter = 0;
                        m_eCurrentIntensityMode = IntensityMode.INCREASING;
                    }
                    ++m_iSettingCounter;
                }

                if (m_iSettingCounter == 3) {
                    // Raise trigger to apply corresponding intensity
                    System.out.println("Intensity Level : "
                            + m_eCurrentIntensityMode.toString());

                    // reset
                    m_iSettingCounter = 0;
                    m_eCurrentIntensityMode = IntensityMode.DISABLED;
                }
            }
        } else {
            System.out.println("Delta is 0 or less!");
        }
    }

}
