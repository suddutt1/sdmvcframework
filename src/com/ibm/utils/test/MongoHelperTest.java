package com.ibm.utils.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.ibm.webapp.bean.UserProfle;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;

import static com.ibm.utils.MongoHelper.*;
import static com.mongodb.client.model.Filters.*;

public class MongoHelperTest {

	public static void main(String[] args) {
		// Writing the test cases in the main
		// Do not want to increase the size and dependency
		Properties props = new Properties();
		/*
		 * props.put(MONGO_CONNECT_URI,
		 * "bluemix-sandbox-dal-9-portal.4.dblayer.com");
		 * props.put(MONGO_DB_NAME, "admin");
		 */
		/*props.put(MONGO_CONNECT_HOST, "ds149258.mlab.com");
		props.put(MONGO_CONNECT_PORT, "49258");
		props.put(MONGO_DB_NAME, "claimdata");
		props.put(MONGO_DB_USERID, "suddutt1");
		props.put(MONGO_DB_PASSWORD, "cnp4test");*/
		//Following is for mlabs ..
		//It seems db version is different
		connectMlabs();

	}

	
	private static void connectMlabs() {
		Properties props = new Properties();
		props.put(MONGO_CONNECT_URI,
				"mongodb://suddutt1:cnp4test@ds149258.mlab.com:49258/claimdata");
		props.put(MONGO_DB_NAME, "claimdata");
		init(props);
		MongoCollection<Document> collection = getCollection("temp1");
		//System.out.println(collection);
		UserProfle userProfile = new UserProfle();
		
		String email = "suddutt1"+System.currentTimeMillis()+"@in.ibm.com";
		userProfile.setEmail(email);
		userProfile.setFname("Sudip");
		userProfile.setLname("Duttatest");
		userProfile.setRole("Admin");
		System.out.println(userProfile.toJson());
		collection.insertOne(Document.parse(userProfile.toJson()));
		UserProfle searchedItem = new UserProfle();
		searchedItem.setEmail(email);
		UserProfle existingItem = new UserProfle();
		existingItem.buildInstance(collection.find(searchedItem.buildFilter()).first());
		System.out.println("After searching" + existingItem.toJson());
		System.out.println("After searching" +existingItem.getObjectId("_id"));
		UserProfle updatedUserProfile = new UserProfle();
		updatedUserProfile.setRole("UpdatedRole");
		updatedUserProfile.setRegId("REG_ID");
		UpdateResult updResult  = collection.updateOne(searchedItem.buildFilter(), new Document("$set",updatedUserProfile));
		System.out.println("Update count"+ updResult.getModifiedCount());
		existingItem = new UserProfle();
		existingItem.buildInstance(collection.find(searchedItem.buildFilter()).first());
		System.out.println("After updating" + existingItem.toJson());
		System.out.println("After update" +existingItem.getObjectId("_id"));
		
		
	}

	
}
