
@Component
public class AESUtil {
	private static final Logger logger = LoggerFactory.getLogger(AESUtil.class);

	@Value("${aes.secret}")
	private String secret;

	@PostConstruct
	public void init() {
	}

	public String encrypt(String value) {
		try {
			byte[] key = secret.getBytes();

			Key aesKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			// encrypt the text
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] encrypted = cipher.doFinal(value.getBytes());

			return Hex.encodeHexString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("encrypt failed", e);
		}
	}

	public String decrypt(String value) {
		if (!StringUtil.isValid(value))
			return value;
		try {
			byte[] key = secret.getBytes();
			Key aesKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			// encrypt the text
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			byte[] encrypted = cipher.doFinal(Hex.decodeHex(value.toCharArray()));

			return new String(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("decrypt failed", e);
		}
	}

	public String encryptBase64UrlSafe(String value) {
		try {
			byte[] key = secret.getBytes();

			Key aesKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			// encrypt the text
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);
			byte[] encrypted = cipher.doFinal(value.getBytes());

			return Base64.getUrlEncoder().encodeToString(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("encrypt failed", e);
		}
	}

	public String decryptBase64UrlSafe(String value) {
		if (!StringUtil.isValid(value))
			return value;
		try {
			byte[] key = secret.getBytes();
			Key aesKey = new SecretKeySpec(key, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			// encrypt the text
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			byte[] encrypted = cipher.doFinal(Base64.getUrlDecoder().decode(value.getBytes()));

			return new String(encrypted);
		} catch (Exception e) {
			throw new RuntimeException("decrypt failed", e);
		}
	}

	public static void main(String[] args) {
		AESUtil util = new AESUtil();
		util.secret = "vF4EURzmThWAg2yK";

		String encrypted = util.encrypt("1:1234");

		System.out.println(util.decrypt(encrypted));
	}
}
