package com.google.hashcode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

public class MainRound {

	public static void main(String[] args) {

		// Read File
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {

			String sCurrentLine;

			Date date = new Date();

			File output = new File("D:\\MyWorks\\GoogleHashCode\\streetview"
					+ date.getTime() + ".txt");
			// if file doesnt exists, then create it
			if (!output.exists()) {
				output.createNewFile();
			}


			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(output), "US-ASCII"));

			br = new BufferedReader(new FileReader(
					"D:\\MyWorks\\GoogleHashCode\\paris_54000.txt"));

			// Read the first Line
			String firstLine = br.readLine();

			// Get junctions number
			Integer junctionsNumber = 0;
			// Get streets number
			Integer streetNumber = 0;
			// Get time to live number, else we will be killed :)
			Integer ttl = 0;
			// Get cars number
			Integer carsNumber = 0;

			// Get cars number
			Integer launchJunction = 0;

			StringTokenizer st = new StringTokenizer(firstLine);

			while (st.hasMoreElements()) {

				junctionsNumber = Integer.parseInt(st.nextElement().toString());

				streetNumber = Integer.parseInt(st.nextElement().toString());

				ttl = Integer.parseInt(st.nextElement().toString());

				carsNumber = Integer.parseInt(st.nextElement().toString());

				launchJunction = Integer.parseInt(st.nextElement().toString());

			}

			// Fill Junctions List
			List<Junction> junctionsList = new ArrayList<Junction>();
			junctionsList.clear();
			// define a bac junction object
			Junction bacJunction = new Junction();
			Double latitude = 0d;
			Double longitute = 0d;

			int currentReadedJunction = 0;
			while (currentReadedJunction < junctionsNumber
					&& (sCurrentLine = br.readLine()) != null) {

				// Get informations aboutt the current junction
				st = new StringTokenizer(sCurrentLine);

				while (st.hasMoreElements()) {

					latitude = Double.parseDouble(st.nextElement().toString());
					bacJunction.setLatitude(latitude);

					longitute = Double.parseDouble(st.nextElement().toString());
					bacJunction.setLongitude(longitute);

					// Add the junction to the List
					junctionsList.add(bacJunction);

				}

				currentReadedJunction = currentReadedJunction + 1;
			}

			// Fill Streets List
			List<Street> streetsList = new ArrayList<Street>();
			// define an bac street object

			Integer startJunction;

			Integer endJunction;

			Integer beDirectional;

			Integer cost;

			Integer distance;

			int currentReadedStreet = 0;

			while ((sCurrentLine = br.readLine()) != null) {

				// Get informations aboutt the current junction
				st = new StringTokenizer(sCurrentLine);

				while (st.hasMoreElements()) {

					Street street = new Street();
					street.setStreetIndex(currentReadedStreet);

					startJunction = Integer.parseInt(st.nextElement()
							.toString());
					street.setStartJunction(startJunction);

					endJunction = Integer.parseInt(st.nextElement().toString());
					street.setEndJunction(endJunction);

					beDirectional = Integer.parseInt(st.nextElement()
							.toString());
					street.setBeDirectional(beDirectional);

					cost = Integer.parseInt(st.nextElement().toString());
					street.setCost(cost);

					distance = Integer.parseInt(st.nextElement().toString());
					street.setDistance(distance);

					// Add the street to the List

					streetsList.add(street);

				}

				currentReadedStreet = currentReadedStreet + 1;

			}

			// Write the number of the cars
			bw.write(carsNumber + "\n");

			// Get start junction

			CarTrajectory carTrajectory = new CarTrajectory();

			Integer currentDistance = 0;

			Integer accumulatedCost = 0;

			// get the List of possible street for the launch junction
			// List<Street> junctionStreets = getJunctionStreets(launchJunction,
			// streetsList);

			// list of street already used
			List<Integer> reservedStreet = new ArrayList<Integer>();

			// Iterate on car number
			for (int i = 0; i < carsNumber; i++) {

				System.out.println("Enter Cars Process number: " + i);
				List<Street> junctionStreets = getJunctionStreets(
						launchJunction, streetsList);
				System.out.println("junctionStreets initial size : "
						+ junctionStreets.size());

				// System.out.println("Initial junction steet size: "+
				// junctionStreets);
				// Initialize
				carTrajectory.getTrajectory().add(launchJunction);

				// get the next possible junction
				while (junctionStreets.size() > 0 && accumulatedCost < ttl) {

					Random r = new Random();
					Street nextStreet = junctionStreets.get(r
							.nextInt(junctionStreets.size()));

					if (accumulatedCost + nextStreet.getCost() < ttl) {
						accumulatedCost = accumulatedCost
								+ nextStreet.getCost();

						// Add the next junction to the trajectory
						carTrajectory.getTrajectory().add(
								nextStreet.getEndJunction());

						reservedStreet.add(nextStreet.getStreetIndex());

						// prepare the next possible street
						junctionStreets.clear();

						junctionStreets = getJunctionStreets(
								nextStreet.getEndJunction(), streetsList);

					} else {
						break;
					}

				}

				// write to number of trajectory for a car
				bw.write(carTrajectory.getTrajectory().size() + "\n");
				// iterate the trajectory list to write in the file
				for (Integer node : carTrajectory.getTrajectory()) {

					bw.write(node.intValue() + "\n");

				}

				carTrajectory.getTrajectory().clear();
				accumulatedCost = 0;

			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			try {
				bw.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		System.out.println("End of the processing");

	}

	private static List<Street> getJunctionStreets(Integer launchJunction,
			List<Street> streetsList) {
		List<Street> startJunctionStreets = new ArrayList<Street>();

		Iterator<Street> iterator = streetsList.iterator();
		while (iterator.hasNext()) {

			Street street = (Street) iterator.next();

			if (street.getStartJunction().intValue() == launchJunction
					.intValue() /*
								 * ||
								 * (street.getEndJunction().equals(launchJunction
								 * ) && street.getBeDirectional().equals(2))
								 */) {

				startJunctionStreets.add(street);
			}
		}

		return startJunctionStreets;
	}

}
