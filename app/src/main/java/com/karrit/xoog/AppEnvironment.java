package com.karrit.xoog;

public enum AppEnvironment {
    PRODUCTION {
        @Override
        public String merchant_Key() {
            return "BIx9qWjk";
        }
        @Override
        public String merchant_ID() {
            return "7169751";
        }
        @Override
        public String furl() {
            return "https://www.payumoney.com/mobileapp/payumoney/failure.php";
        }

        @Override
        public String surl() {
            return "https://www.payumoney.com/mobileapp/payumoney/success.php";
        }

        @Override
        public String salt() {
            return "QaqFuBPROO";
        }

        @Override
        public boolean debug() {
            return false;
        }
    };

    public abstract String merchant_Key();

    public abstract String merchant_ID();

    public abstract String furl();

    public abstract String surl();

    public abstract String salt();

    public abstract boolean debug();


}
