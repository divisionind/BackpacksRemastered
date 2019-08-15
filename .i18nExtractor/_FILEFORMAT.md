# File format
This is a small write-up on the file format for the google translate cache files.

## Info
- Byte order: Big endian
- _prev_ in the following stands for the value of the previous integer

```
Int32[4] - number of translations cached
foreach translation:
    Int32[4] - number of bytes belonging to the following string
    byte[prev] - UTF-8 encoded string (the original text)
    Int32[4] - number of bytes belonging to the following string
    byte[prev] - UTF-8 encoded string (translation of the original text provided by google)
end
```

I considered adding a time value to the format so that cached translations would eventually expire. However, I
decided against it because of how easy it is to refresh translations by simply deleting the cache. Also, all
integers are signed due to Java not supporting unsigned integers.