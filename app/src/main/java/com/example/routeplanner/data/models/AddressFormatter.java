package com.example.routeplanner.data.models;

import com.example.routeplanner.data.pojos.Address;

public class AddressFormatter {

    /**
     * Formats the given address to a standard format of  "street, postCode city, country"
     */
    public Address formatAddress(String addressString){

        Address address;

        if(addressString == null){
            address = null;
        }else{

            address = new Address();

//            System.out.println("complete address: "+address);
//            System.out.println("");

            int commasCount = 0;

            for(int i = 0; i < addressString.length(); i++) {
                if(addressString.charAt(i) == ',') commasCount++;
            }

//            System.out.println("Commas count: "+commasCount);
//            System.out.println("");

            if(commasCount == 1){

//                System.out.println("Address to be process 1: "+address);
//                System.out.println("");

                address.setAddress(addressString);
                address.setStreet(addressString.split(",")[0]);
                address.setPostCode("");
                address.setCity(addressString.split(",")[1].substring(1));
                address.setCountry("");

            }else {

                if (commasCount==3){
                    addressString = addressString.split(",")[1].substring(1) + "," +
                            addressString.split(",")[2]+ "," +
                            addressString.split(",")[3];
                }

//                System.out.println("Address to be process 2: "+address);
//                System.out.println("");

                address.setStreet(addressString.split(",")[0]);
                address.setPostCode(addressString.split(",")[1].substring(1, 8));
                address.setCity(addressString.split(",")[1].substring(9));
                address.setCountry(addressString.split(",")[2].substring(1));

                if (address.getPostCode().substring(0, 4).matches("[0-9]+")) {

                    String postCodeLettersHolder = address.getPostCode().substring(4, 7).replaceAll(" ", "");

                    address.setPostCode(address.getPostCode().substring(0, 4));

                    if (Character.isUpperCase(postCodeLettersHolder.charAt(0))) {

                        if (Character.isUpperCase(postCodeLettersHolder.charAt(1))) {
                            address.setPostCode(address.getPostCode() + " " + postCodeLettersHolder);

                            if (addressString.split(",")[1].substring(1, 7).contains(address.getPostCode().replaceAll(" ", ""))) {
                                address.setCity(addressString.split(",")[1].substring(8));
                            }

                        } else {
                            address.setCity(addressString.split(",")[1].substring(6));
                        }

                    }

                    address.setAddress(
                            address.getStreet() + ", " +
                                    address.getPostCode() + " " +
                                    address.getCity() + ", " +
                                    address.getCountry()
                    );

//                    System.out.println("2: " + getformattedAddress());
//                    System.out.println(postCodeLettersHolder);
//                    System.out.println(getPostCode());
//                    System.out.println(getCity());

                }

            }

        }

        return address;
    }
}
