import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

/**
 * Converts subnet masks between CIDR, octet, and binary formats.
 * Handles inputs like "/24", "255.255.255.0", or "11111111.11111111.11111111.00000000".
 */
public class SubNetMaskTranslate {

    public enum SubnetMaskType {
        OCTET,  // e.g., "255.255.255.0"
        BIN,    // e.g., "11111111.11111111.11111111.00000000"
        CIDR    // e.g., "/24"
    }

    // Constants
    private static final int BITS_PER_OCTET = 8;
    private static final int MAX_CIDR = 32;
    private static final int MAX_OCTET = 255;

    // State
    private SubnetMaskType type;
    private String octetFormat;
    private String cidrFormat;
    private String binFormat;

    //helpers
    private final ArrayList<String> octetParts = new ArrayList<>();
    private String[] userInput;
    private int oneCount;
    private int zeroCount;

    // Core Methods
    public SubNetMaskTranslate(String expression) {
        parse(expression);
    }

    /**
     * Re-process a new subnet mask (allows object reuse).
     * @throws IllegalArgumentException for invalid CIDR, octets, or binary.
     */
    public void parse(String expression) {
        resetState();
        userInput = expression.split("");
        defineSubnetMaskType();

        try {
            switch (type) {
                case CIDR -> processCidr(expression);
                case OCTET -> processOctet(expression);
                case BIN -> processBinary(expression);
                case null -> {}
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid subnet mask: " + expression, e);
        }
    }

    // Getters
    public String getBinFormat() { return binFormat; }
    public String getCidrFormat() { return cidrFormat; }
    public String getOctetFormat() { return octetFormat; }

    // Debugging
    @Override
    public String toString() {
        return String.format("CIDR: %s | Octet: %s | Binary: %s",
                cidrFormat, octetFormat, binFormat);
    }

    // Private Helpers
    private void resetState() {
        octetParts.clear();
        oneCount = 0;
        zeroCount = 0;
        octetFormat = null;
        cidrFormat = null;
        binFormat = null;
    }

    private void defineSubnetMaskType() {
        if (isCidrInput()) type = SubnetMaskType.CIDR;
        else if (isOctetInput()) type = SubnetMaskType.OCTET;
        else if (isBinaryInput()) type = SubnetMaskType.BIN;
        else throw new IllegalArgumentException("Unknown subnet mask format");
    }

    private boolean isCidrInput() {
        return (userInput[0].equals("/") || userInput[0].equals("\\"))
                && userInput.length <= 3;  // e.g., "/24"
    }

    private boolean isOctetInput() {
        return userInput[0].equals("2")  // Quick check for "255.x.x.x"
                && String.join("", userInput).contains(".");
    }

    private boolean isBinaryInput() {
        String joined = String.join("", userInput);
        return joined.replace(".", "").matches("[01]{32}");
    }

    // Processors
    private void processCidr(String cidr) {
        int cidrValue = Integer.parseInt(cidr.substring(1));
        validateCidr(cidrValue);

        oneCount = cidrValue;
        zeroCount = MAX_CIDR - oneCount;
        cidrFormat = "/" + oneCount;
        octetFormat = buildOctetFormat();
        binFormat = buildBinaryFormat();
        binFormat = formatBinaryWithDots(binFormat);
    }

    private void processOctet(String octet) {
        String[] octets = octet.split("\\.");
        if (octets.length != 4) throw new IllegalArgumentException("Invalid octet count");

        for (String oct : octets) {
            int value = Integer.parseInt(oct);
            validateOctet(value);
            oneCount += Integer.bitCount(value);
        }

        zeroCount = MAX_CIDR - oneCount;
        cidrFormat = "/" + oneCount;
        binFormat = buildBinaryFormat();
        binFormat = formatBinaryWithDots(binFormat);
        octetFormat = octet;  // Original input was already in octet format
    }

    private void processBinary(String binary) {
        String cleanBinary = binary.replace(".", "");
        if (cleanBinary.length() != 32) throw new IllegalArgumentException("Binary must be 32 bits");

        for (char bit : cleanBinary.toCharArray()) {
            if (bit == '1') oneCount++;
            else if (bit == '0') zeroCount++;
            else throw new IllegalArgumentException("Binary must contain only 0s and 1s");
        }

        cidrFormat = "/" + oneCount;
        octetFormat = buildOctetFormat();
        binFormat = formatBinaryWithDots(cleanBinary);
    }

    // Builders
    private String buildOctetFormat() {
        octetParts.clear();
        int fullOctets = oneCount / BITS_PER_OCTET;
        int remainingBits = oneCount % BITS_PER_OCTET;

        // Add full 255 octets
        for (int i = 0; i < fullOctets; i++) {
            octetParts.add("255");
        }

        // Add partial octet
        if (remainingBits > 0) {
            int partialOctet = (MAX_OCTET << (BITS_PER_OCTET - remainingBits)) & MAX_OCTET;
            octetParts.add(String.valueOf(partialOctet));
        }

        // Fill remaining with 0s
        while (octetParts.size() < 4) {
            octetParts.add("0");
        }

        return String.join(".", octetParts);
    }

    private String buildBinaryFormat() {
        return "1".repeat(oneCount) + "0".repeat(zeroCount);
    }

    private String formatBinaryWithDots(String binary) {
        return binary.replaceAll("(.{8})", "$1.").substring(0, 35);  // Trim trailing dot
    }

    // Validators
    private void validateCidr(int cidr) {
        if (cidr < 0 || cidr > MAX_CIDR) {
            throw new IllegalArgumentException("CIDR must be 0-32");
        }
    }

    private void validateOctet(int octet) {
        if (octet < 0 || octet > MAX_OCTET) {
            throw new IllegalArgumentException("Octet must be 0-255");
        }
    }

    // ================== MAIN METHOD ================== //

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter subnet mask: ");
        String input = scanner.next();
        SubNetMaskTranslate translator = new SubNetMaskTranslate(input);

        do {
            System.out.println("\nConversion Results:");
            System.out.println("Type: " + translator.type);
            System.out.println("Ones/Zeroes: " + translator.oneCount + " / " + translator.zeroCount);
            System.out.println("CIDR: " + translator.getCidrFormat());
            System.out.println("Octet: " + translator.getOctetFormat());
            System.out.println("Binary: " + translator.getBinFormat());

            System.out.print("Enter subnet mask: ");
            input = scanner.next();
            translator.parse(input);

        } while (!Objects.equals(input, "0"));
    }
}