NBTMap map = new NBTMap();
float fl = 30F;
map.setNBT(NBTType.FLOAT, "test", fl);

int iterations = 300000;

// warm up
for (int i = 0; i < iterations; i++) {
    float f = map.getNBT(float.class, "test");
    float f2 = (float)map.getNBT(NBTType.FLOAT, "test");
}

// now time getting the value
long time;
time = System.currentTimeMillis();
for (int i = 0; i < iterations; i++) {
    float f = map.getNBT(float.class, "test");
}
System.out.println("Time1=" + (System.currentTimeMillis() - time));

time = System.currentTimeMillis();
for (int i = 0; i < iterations; i++) {
    float f = (float)map.getNBT(NBTType.FLOAT, "test");
}
System.out.println("Time2=" + (System.currentTimeMillis() - time));

