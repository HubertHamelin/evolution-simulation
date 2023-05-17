import java.io.Serializable;

public class Genome implements Serializable {

    String[] sequences;
    String[] sequencesHexa;

    public Genome(int nbConnection, int sequenceLength) {
        this.sequencesHexa = new String[nbConnection];
        for (int i = 0; i < nbConnection; i++) {
            this.sequencesHexa[i] = this.initRandomSequence(sequenceLength);
        }
        this.setSequencesFromHexa();
    }

    public Genome(String[] hexa) {
        this.sequencesHexa = hexa;
        this.setSequencesFromHexa();
    }

    private void setSequencesFromHexa() {
        int nbConnection = this.sequencesHexa.length;
        this.sequences = new String[nbConnection];
        for (int i = 0; i < nbConnection; i++) {
            this.sequences[i] = this.hexaToBitSequence(this.sequencesHexa[i]);
        }
    }

    public void mutation() {
        // Select a random hexadecimal sequence
        int randomSeqIndex = (int) (Math.random() * this.sequencesHexa.length);
        // Select a random element of the sequence
        int randomElementIndex = (int) (Math.random() * this.sequencesHexa[randomSeqIndex].length());
        // Randomly add or subtract 1 if still in the range of hexadecimal element
        int ElementValue = 0;
        try {
            // ElementValue = Integer.parseInt(this.sequencesHexa[randomSeqIndex].substring(randomElementIndex, randomElementIndex));
            ElementValue = Integer.parseInt(String.valueOf(this.sequencesHexa[randomSeqIndex].charAt(randomElementIndex)), 16);
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        if (Math.random() > 0.5)
            ElementValue = Integer.min(ElementValue + 1, 15);
        else
            ElementValue = Integer.max(ElementValue - 1, 0);
        StringBuilder sb = new StringBuilder(this.sequencesHexa[randomSeqIndex]);
        sb.setCharAt(randomElementIndex, Integer.toHexString(ElementValue).charAt(0));
        this.sequencesHexa[randomSeqIndex] = sb.toString();

        // Regenerate the bits sequence based on the altered hexadecimal sequence
        this.sequences = new String[this.sequencesHexa.length];
        for (int i = 0; i < this.sequencesHexa.length; i++) {
            this.sequences[i] = this.hexaToBitSequence(this.sequencesHexa[i]);
        }
    }

    public void printGenome() {
        StringBuilder hexa = new StringBuilder();
        for (String part : this.sequencesHexa) {
            hexa.append(part).append(" ");
        }
        System.out.println(hexa);
        StringBuilder bits = new StringBuilder();
        for (String part : this.sequences) {
            bits.append(part).append(" ");
        }
        System.out.println(bits);
    }

    private String initRandomSequence(int sequenceLength) {
        StringBuilder seq = new StringBuilder();
        for (int i = 0; i < sequenceLength; i++) {
            int random = (int) (Math.random() * 16);
            String hex = Integer.toHexString(random);
            seq.append(hex.toUpperCase());
        }
        return seq.toString();
    }

    private String hexaToBitSequence(String hexa) {
        try {
            StringBuilder seq = new StringBuilder();
            for (int i = 0; i < hexa.length(); i++) {
                int nb = Integer.parseInt(String.valueOf(hexa.charAt(i)), 16);
                StringBuilder subseq = new StringBuilder(Integer.toBinaryString(nb));
                // Pad the sub-sequence with zeros until it as 4 elements
                while (subseq.length() < 4) {
                    subseq.insert(0, "0");
                }
                seq.append(subseq);
            }
            return seq.toString();
        } catch (Exception e) {
            System.out.println(e);
            return " ";
        }
    }
}
