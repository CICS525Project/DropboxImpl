// File Name GreetingServer.java

import java.net.*;
import java.io.*;

import authentication.Authentication;

public class GreetingServer extends Thread {
	private ServerSocket serverSocket;
	public static String storageConnectionString = "DefaultEndpointsProtocol=http;"
			+ "AccountName=cics525group6;"
			+ "AccountKey=gAI6LQdhg/WnhMDPa46IYr66NLODOnMoP/LXJGsBtpYOCtO7ofKCL3YuOOsmLyUyHVf/63BNVI9H/ZI4OSgILg==";

	private static String container = "https://cics525group6.blob.core.windows.net/mycontainer";

	public GreetingServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		// serverSocket.setSoTimeout(1000000000);
	}

	public void run() {
		Socket server = null;
		while (true) {
			try {
				System.out.println("Waiting for client on port "
						+ serverSocket.getLocalPort() + "...");
				server = serverSocket.accept();

				while (!server.isClosed()) {

					System.out.println("Connected to "
							+ server.getRemoteSocketAddress());

					// receive and print message from client
					InputStream inFromClient = server.getInputStream();
					DataInputStream in = new DataInputStream(inFromClient);
					String request;
					// read request from client

					try {
						// read request from client
						request = in.readUTF();
					} catch (EOFException e) {
						System.out.println("Connection with "
								+ server.getRemoteSocketAddress() + " closed unexpectedly");
						break;
					}
					System.out.println("Client says: " + request);

					// prepare output stream to send response to client
					DataOutputStream out = new DataOutputStream(
							server.getOutputStream());

					// Split request
					// part[0] request type
					// part[1] username
					// part[2] password
					Authentication auth = new Authentication();

					String[] part = request.split(",");

					// exit upon client request
					if (part[0].equalsIgnoreCase("Q")) {
						System.out.println("Connection with "
								+ server.getRemoteSocketAddress() + " closed by Client");
						server.close();
						break;
					}

					// for authentication
					else if (part[0].equalsIgnoreCase("auth")) {
						if (auth.validUser(part[1], part[2])) {
							System.out.println("Username " + part[1] + " and "
									+ "password " + part[2] + " validated");
							out.writeUTF("true");

							// continue to receive requests from authorized
							// client
							while (true) {
								inFromClient = server.getInputStream();
								in = new DataInputStream(inFromClient);

								try {
									// read request from client
									request = in.readUTF();
								} catch (EOFException e) {
									System.out
											.println("Sesion with "
													+ "Username " + part[1]
													+ " and " + "password "
													+ part[2] + " closed unexpectedly");
									break;
								}
								System.out.println("Client says: " + request);
								// valid request options are Q, upload and
								// download

								// upload request
								if (request.equalsIgnoreCase("Q")) {
									System.out
											.println("Client session exit requested by "+ "Username " + part[1]);
									
									break;
								} else if (request.equalsIgnoreCase("upload")) {
									out.writeUTF(container + ","
											+ storageConnectionString);
									System.out
											.println("Upload information sent to client");
								}
								// download request
								else if (request.equalsIgnoreCase("download")) {
									out.writeUTF(container + ","
											+ storageConnectionString);
									System.out
											.println("Download information sent to client");
								}
								// if other send null response and inform error
								// to stdout
								else {
									out.writeUTF("Operation not supported");
									System.out
											.println("Operation " +part[0] + " not supported");
								}
								System.out.println("Waiting input from sesion with "
										+ "Username " + part[1]
										+ " and " + "password "
										+ part[2]);
							}

						}
						// if user validation fails
						else {
							System.out
									.println("Username or password incorrect");
							out.writeUTF("false");
						}

					}
					// case for account creation
					else if (part[0].equalsIgnoreCase("crea")) {
						// if user account creation succeeds
						if (auth.createUser(part[1], part[2])) {
							System.out.println("New account created:");
							System.out.println("User: " + part[1]);
							System.out.println("Password: " + part[2]);
							out.writeUTF("true");
						} else {
							System.out.println("Fail to create account");
							out.writeUTF("false");
						}
					}
					// if the operation request from the client is different
					// from auth and crea
					else {
						System.out.println("Invalid operation request: "
								+ part[0]);
						out.writeUTF("false");
					}

				}
				server.close();
				// catch lost socket connections
			} catch (SocketException e) {
				if (e.getMessage().equals("Connection reset")) {
					System.out.println("Connection with client "
							+ server.getRemoteSocketAddress() + " reseted");
				} else {
					e.printStackTrace();
				}
				// catch any other exception
			} catch (IOException e) {
				e.printStackTrace();

			}

		}

	}

	// main method creates a thread for every connection
	// runs on port 12345
	public static void main(String[] args) {
		int port = 12345;
		try {
			Thread t = new GreetingServer(port);
			t.start();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
