package com.karrit.xoog;



public class Address {

        private String Address_title;

        private String Address_full;

        public Address(String Address_title, String Address_full) {
            this.Address_title = Address_title;
            this.Address_full = Address_full;

        }




       public String getAddress_title(){
            return Address_title;}

    public String getAddress_full() {
        return Address_full;
    }

    public void setAddress_full(String address_full) {
        Address_full = address_full;
    }

    public void setAddress_title(String address_title) {
        Address_title = address_title;
    }


}
