#ifndef bit_array_
#define bit_array_


#include <stdio.h>
#include <string.h>
#include <iostream>


class BitArray {
private:
    int num_bits;  // Number of bits in the array
    std::vector<unsigned long> data;  // Internal storage for the bit array, organized in blocks of unsigned long

public:
    // Accessor to retrieve the underlying data vector
    std::vector<unsigned long> get_data() {
        return data;
    }
    
    // Default constuctor
    BitArray();
    
    // Constructs a bit array with a specified number of bits. Optionally initializes the first `sizeof(long)` bits with `value`.
    explicit BitArray(int number_bits, unsigned long value = 0);
    
    BitArray(const BitArray& b);  // Copy constructor
    ~BitArray();  // Destructor to release resources

    // This class provides a proxy object that allows reading and modifying a single bit within a BitArray
    class BitReference {
    private:
        int bit_idx_;           // Position of the bit within an unsigned long block
        unsigned long& block_idx;    // Reference to the unsigned long that holds the bit

    public:
        BitReference(unsigned long& block, int bit_idx);  // Constructor initializes block and bit index
        BitReference& operator=(bool val);  // Assignment operator to set bit's value

        operator bool() const;  // Conversion to bool for reading bit values
    };
   
    BitReference operator[](int i);  // Overloads the subscript operator for accessing individual bits
  
    class Iterator {
     private:
         BitArray* bit_array;  // Points to the bit array being iterated over
         int index;  // Current position in the bit array

     public:
         Iterator(BitArray* array, int idx);  // Constructor initializes bit array and index
         
         bool operator!=(const Iterator& array2) const;  // Comparison operator for inequality
         bool operator==(const Iterator& array2) const;  // Comparison operator for equality
         
         BitReference& operator*();  // Dereference operator to access the current bit
         
         Iterator& operator++();  // Increment operator to move to the next bit
     };
     
     Iterator begin();  // Returns an iterator to the start of the bit array
     Iterator end();  // Returns an iterator to one past the end of the bit array
    
    BitArray& operator=(const BitArray& b);  // Assignment operator for deep copy
    
    // Bitwise NOT operator, returns an inverted copy of the array
    BitArray operator~() const;

    // Const subscript operator for accessing a bit's value at index `i`
    bool operator[](int i) const;

    // Bitwise operations on arrays of equal size. Ensures both arrays are of the same size.
    BitArray& operator&=(const BitArray& b);  // Bitwise AND assignment
    BitArray& operator|=(const BitArray& b);  // Bitwise OR assignment
    BitArray& operator^=(const BitArray& b);  // Bitwise XOR assignment

    // Bit-shifting operations with zero-fill
    BitArray& operator<<=(int n);  // Left shift assignment
    BitArray& operator>>=(int n);  // Right shift assignment
    BitArray operator<<(int n) const;  // Left shift with a new BitArray result
    BitArray operator>>(int n) const;  // Right shift with a new BitArray result
    
    // Swaps contents with another BitArray
    void swap(BitArray& b);
    
    // Resizes the array to hold `number_bits`, initializing any new bits with `value`
    void resize(int number_bits, bool value = false);
    // Clears all bits in the array, resetting the size to zero
    void clear();
    
    // Sets the bit at index `n` to `val` (defaults to true)
    BitArray& set(int n, bool val = true);
    // Sets all bits in the array to true
    BitArray& set();

    // Adds a bit to the end of the array, reallocating memory as necessary
    void push_back(bool bit);
    
    // Resets the bit at index `n` to false
    BitArray& reset(int n);
    // Resets all bits in the array to false
    BitArray& reset();
    
    // Returns true if any bit in the array is true
    bool any() const;
    // Returns true if all bits in the array are false
    bool none() const;
    
    int size() const;  // Returns the number of bits in the array
    bool empty() const;  // Checks if the array has no bits
    
    // Counts the number of true bits in the array
    int count() const;
    
    // Returns a string representation of the bit array
    std::string to_string() const;
    
    // Equality operators for comparing two BitArrays
    friend bool operator==(const BitArray &a, const BitArray &b);
    friend bool operator!=(const BitArray &a, const BitArray &b);
};

// Global operators for bitwise operations on two BitArrays, returning a new BitArray result
bool operator==(const BitArray & a, const BitArray & b);
bool operator!=(const BitArray & a, const BitArray & b);

BitArray operator&(const BitArray& b1, const BitArray& b2);  // Bitwise AND
BitArray operator|(const BitArray& b1, const BitArray& b2);  // Bitwise OR
BitArray operator^(const BitArray& b1, const BitArray& b2);  // Bitwise XOR


#endif
