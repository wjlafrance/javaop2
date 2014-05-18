package com.javaop.BNetLogin.password;

import java.security.MessageDigest;

import com.javaop.util.BigIntegerEx;
import com.javaop.util.Buffer;

public class SRP
{

	public static final int BIGINT_SIZE = 32;
	public static final int SHA_DIGESTSIZE = 20;

	private static final byte[] generatorRaw = new byte[] { 47 };
	private static final byte[] modulusRaw = new byte[] { (byte) 0x87, (byte) 0xc7, (byte) 0x23, (byte) 0x85,
			(byte) 0x65, (byte) 0xf6, (byte) 0x16, (byte) 0x12, (byte) 0xd9, (byte) 0x12, (byte) 0x32, (byte) 0xc7,
			(byte) 0x78, (byte) 0x6c, (byte) 0x97, (byte) 0x7e, (byte) 0x55, (byte) 0xb5, (byte) 0x92, (byte) 0xa0,
			(byte) 0x8c, (byte) 0xb6, (byte) 0x86, (byte) 0x21, (byte) 0x03, (byte) 0x18, (byte) 0x99, (byte) 0x61,
			(byte) 0x8b, (byte) 0x1a, (byte) 0xff, (byte) 0xf8 };
	// I is actually H(N) xor'd with H(g), byte by byte
	private static final byte[] I = new byte[] { (byte) 0x6c, (byte) 0xe, (byte) 0x97, (byte) 0xed, (byte) 0xa,
			(byte) 0xf9, (byte) 0x6b, (byte) 0xab, (byte) 0xb1, (byte) 0x58, (byte) 0x89, (byte) 0xeb, (byte) 0x8b,
			(byte) 0xba, (byte) 0x25, (byte) 0xa4, (byte) 0xf0, (byte) 0x8c, (byte) 0x1, (byte) 0xf8 };

	private static final BigIntegerEx N = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, modulusRaw);
	private static final BigIntegerEx g = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, generatorRaw);

	private final String username;
	private final String password;

	private final BigIntegerEx a;

	public static void main(String[] args)
	{
		SRP srp = new SRP("username", "password");
		byte[] salt = new byte[32];

		System.out.println("v: " + srp.get_v(new byte[32]).toString(16));
		System.out.println("A: " + new Buffer(srp.get_A()));
		System.out.println("u: " + srp.get_u(salt).toString(16));
		System.out.println("S: " + new Buffer(srp.get_S(salt, salt)));
		System.out.println("K: " + new Buffer(srp.get_K(srp.get_S(salt, salt))));
		System.out.println("M1: " + new Buffer(srp.getM1(salt, salt)));
	}

	public SRP(String username, String password)
	{
		this.username = username.toUpperCase();
		this.password = password.toUpperCase();

		// a = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, "0");
		a = new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, BIGINT_SIZE * 8);
	}



	public BigIntegerEx get_x(byte[] salt)
	{
		// x = H(s, H(C, ":", P))

		MessageDigest mdx = getSHA1();
		mdx.update(username.getBytes());
		mdx.update(":".getBytes());
		mdx.update(password.getBytes());
		byte[] hash = mdx.digest();

		mdx = getSHA1();
		mdx.update(salt);
		mdx.update(hash);
		hash = mdx.digest();



		return new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, hash);
	}

	public BigIntegerEx get_v(byte[] salt)
	{
		// v = g^x % N
		return g.modPow(get_x(salt), N);
	}


	public byte[] get_A()
	{
		// A = g^a % N
		return g.modPow(a, N).toByteArray();
	}

	public BigIntegerEx get_u(byte[] B)
	{
		// u = The first 4 bytes of H(B)
		byte[] hash = getSHA1().digest(B); // Get the SHA-1 digest of B
		byte[] u = new byte[4]; // Allocate 4 bytes for U
		u[0] = hash[3];
		u[1] = hash[2];
		u[2] = hash[1];
		u[3] = hash[0];

		return new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, u);
	}



	public byte[] get_S(byte[] s, byte[] B)
	{
		// S = (B - v)^(a + ux) % N

		BigIntegerEx S_base = N.add(new BigIntegerEx(BigIntegerEx.LITTLE_ENDIAN, B)).subtract(get_v(s)).mod(N);
		BigIntegerEx S_exp = a.add(get_u(B).multiply(get_x(s)));
		return S_base.modPow(S_exp, N).toByteArray();
	}

	public byte[] get_K(byte[] S)
	{
		// K is double, interlaced hash based on S
		// Basically, Every second character of S is put into an array and hashed, and then the
		// rest are put into another array and hashed, then they are put into their respective
		// positions in k
		byte[] K = new byte[40];
		byte[] hbuf1 = new byte[16];
		byte[] hbuf2 = new byte[16];

		for(int i = 0; i < hbuf1.length; i++)
		{
			hbuf1[i] = S[i * 2];
			hbuf2[i] = S[(i * 2) + 1];
		}

		byte[] hout1 = getSHA1().digest(hbuf1);
		byte[] hout2 = getSHA1().digest(hbuf2);

		for(int i = 0; i < hout1.length; i++)
		{
			K[i * 2] = hout1[i];
			K[(i * 2) + 1] = hout2[i];
		}

		return K;
	}

	public byte[] getM1(byte[] s, byte[] B)
	{
		// M[1] = H(I, H(C), s, A, B, K)

		MessageDigest totalCtx = getSHA1();
		totalCtx.update(I);
		totalCtx.update(getSHA1().digest(username.getBytes()));
		totalCtx.update(s);
		totalCtx.update(get_A());
		totalCtx.update(B);
		totalCtx.update(get_K(get_S(s, B)));

		return totalCtx.digest();
	}

	public byte[] getM2(byte[] s, byte[] B)
	{
		// M[2] = H(A | M[1] | K)
		byte[] A = get_A();
		byte[] M = getM1(s, B);
		byte[] K = get_K(get_S(s, B));

		MessageDigest M2 = getSHA1();
		M2.update(A);
		M2.update(M);
		M2.update(K);

		return M2.digest();
	}

	private MessageDigest getSHA1()
	{
		try
		{
			return MessageDigest.getInstance("SHA-1");
		}
		catch(Exception e)
		{
			System.err.println("Apparently SHA-1 isn't installed");
			System.exit(0);
			throw new RuntimeException();
		}
	}
}
