#include <iostream>
#include "bit_array.hpp"

#define BLOCK_SIZE (sizeof(unsigned long) * 8)

BitArray::BitArray() {
    num_bits = 0;
}


BitArray::BitArray(int number_bits, unsigned long value) {
    if (number_bits < 0)
        throw std::invalid_argument("Invalid number of bits.");
        
    num_bits = number_bits;
    if (num_bits) {
        data.resize((number_bits/BLOCK_SIZE) + 1, 0);
        data[0] = value;
    }
}


BitArray::BitArray(const BitArray& b) {
    num_bits = b.num_bits;
    data = b.data;
}


BitArray::~BitArray() = default;


//  defining BitReference methods and operators

BitArray::BitReference::BitReference(unsigned long& block, int bit_idx)
    : block_idx(block), bit_idx_(bit_idx) {}



BitArray::BitReference::operator bool() const {
    return (block_idx & (1UL << bit_idx_)) != 0;
}


BitArray::BitReference BitArray::operator[](int i) {
    if (i < 0)
        throw std::out_of_range("Invalid index.");
    if (i >= num_bits)
        throw std::out_of_range("Bit index out of range");
    
    return BitReference(data[i / BLOCK_SIZE], i % BLOCK_SIZE);
}


BitArray::BitReference& BitArray::BitReference::operator=(bool val) {
    if (val)
        block_idx |= (1UL << bit_idx_);
    else
        block_idx &= ~(1UL << bit_idx_);
    
    return *this;
}


//  defining Iterator methods and operators


BitArray::Iterator::Iterator(BitArray* array, int idx) {
    bit_array = array;
    index = idx;
}


bool BitArray::Iterator::operator!=(const BitArray::Iterator& other) const {
    return index != other.index;
}


bool BitArray::Iterator::operator==(const BitArray::Iterator& other) const {
    return index == other.index;
}


BitArray::BitReference& BitArray::Iterator::operator*() {
    if (index < 0 || index >= bit_array->num_bits)
        throw std::out_of_range("Index out of range.");
    
    // Возвращаем ссылку на BitReference, чтобы избежать создания временного объекта
    return *(new BitReference(bit_array->data[index / BLOCK_SIZE], index % BLOCK_SIZE));
}





BitArray::Iterator& BitArray::Iterator::operator++() {
    ++index;
    return *this;
}


BitArray::Iterator BitArray::begin() {
    return Iterator(this, 0);
}


BitArray::Iterator BitArray::end() {
    return Iterator(this, num_bits);
}


//  defining BitArray operators

BitArray& BitArray::operator=(const BitArray& b) {
    data = b.data;
    num_bits = b.num_bits;
    
    return *this;
}


BitArray BitArray::operator~() const {
    BitArray result(*this);
    for (int i = 0; i < result.data.size(); i++) {
        (result.data)[i] = ~((result.data)[i]);
    }
    
    return result;
}


bool BitArray::operator[](int i) const {
    if (i < 0)
        throw std::out_of_range("Invalid index.");
    if (i >= num_bits)
        throw std::out_of_range("Index out of range.");
    
    return data[i / BLOCK_SIZE] & (1UL << (i % BLOCK_SIZE));
}


BitArray& BitArray::operator&=(const BitArray& b) {
    if ((*this).size() != b.size())
        throw std::invalid_argument("Invald array sizes.");
    
    for (int i = 0; i < data.size(); i++)
            data[i] &= b.data[i];
    
    return *this;
}


BitArray& BitArray::operator|=(const BitArray& b) {
    if ((*this).size() != b.size())
        throw std::invalid_argument("Invald array sizes.");
    
    for (int i = 0; i < data.size(); i++)
            data[i] |= b.data[i];
    
    return *this;
}


BitArray& BitArray::operator^=(const BitArray& b) {
    if ((*this).size() != b.size())
        throw std::invalid_argument("Invald array sizes.");
    
    for (int i = 0; i < data.size(); i++)
            data[i] ^= b.data[i];
    
    return *this;
}


BitArray& BitArray::operator<<=(int n) {
    BitArray new_array(num_bits);
    
    for (int i = 0; i < num_bits - n; i++)
        new_array[n + i] = (*this)[i] ? true : false;
    
    (*this).swap(new_array);
    return *this;
}

BitArray& BitArray::operator>>=(int n) {
    BitArray new_array(num_bits);
    
    for (int i = 0; i < num_bits - n; i++)
        new_array[i] = (*this)[n + i] ? true : false;
    
    (*this).swap(new_array);
    return *this;
}


BitArray BitArray::operator<<(int n) const {
    BitArray result(*this);
    return result <<= n;
}


BitArray BitArray::operator>>(int n) const {
    BitArray result(*this);
    return result >>= n;
}


//  defining BitArray methods

void BitArray::swap(BitArray& b) {
    std::swap(data, b.data);
    std::swap(num_bits, b.num_bits);
}


void BitArray::resize(int number_bits, bool value) {
    if (number_bits < 0)
        throw std::invalid_argument("Invalid number of bits.");
    
    std::vector<unsigned long> new_data((number_bits - 1) / BLOCK_SIZE + 1, value ? ULONG_MAX : 0);
    int saved_bits = std::min(num_bits, number_bits);
    
    for (int i = 0; i < saved_bits; ++i) {
        new_data[i / BLOCK_SIZE] &= ~(1UL << (i % BLOCK_SIZE));
        if (data[i / BLOCK_SIZE] & (1UL << (i % BLOCK_SIZE)))
            new_data[i / BLOCK_SIZE] |= (1UL << (i % BLOCK_SIZE));
    }
    data.swap(new_data);
    num_bits = number_bits;
}


void BitArray::clear() {
    data.clear();
    num_bits = 0;
}


BitArray& BitArray::set(int n, bool val) {
    if (n < 0)
        throw std::out_of_range("Invalid index.");
    if (n >= num_bits)
        throw std::out_of_range("Index out of range.");
    
    if (val)
        data[n / BLOCK_SIZE] |= (1UL << (n % BLOCK_SIZE));
    else
        data[n / BLOCK_SIZE] &= ~(1UL << (n % BLOCK_SIZE));
    
    return *this;
}


BitArray& BitArray::set() {
    std::fill(data.begin(), data.end(), ULONG_MAX);
    return *this;
}


void BitArray::push_back(bool bit) {
    resize(num_bits + 1);
    set(num_bits - 1, bit);
}


BitArray& BitArray::reset(int n) {
    return set(n, false);
}


BitArray& BitArray::reset() {
    std::fill(data.begin(), data.end(), 0);
    return *this;
}


bool BitArray::any() const {
    if (!data.size())
        return false;
    
    for (int i = 0; i < data.size() - 1; i++)
        if (data[i])
            return true;
    
    for (int i = 0; i < (num_bits % BLOCK_SIZE); i++)
        if (data.back() & (1 << i))
            return true;
    
    return false;
}


bool BitArray::none() const {
    return !any();
}


int BitArray::size() const {
    return num_bits;
}


bool BitArray::empty() const {
    return size() ? false : true;
}


int BitArray::count() const {
    int counter = 0;
    
    for (int i = 0; i < num_bits; i++)
        if ((*this)[i])
            counter++;
    
    return counter;
}


std::string BitArray::to_string() const {
    std::string str_array;
    
    for (int i = num_bits - 1; i >= 0; i--){
        if ((*this)[i])
                str_array += "1";
        else
            str_array += "0";
    }
    
    return str_array;
}

bool operator==(const BitArray & a, const BitArray & b) {
    return a.size() == b.size() && a.data == b.data;
}

bool operator!=(const BitArray & a, const BitArray & b) {
    return !(a == b);
}

BitArray operator&(const BitArray& b1, const BitArray& b2) {
    BitArray result(b1);
    result &= b2;
    return result;
}


BitArray operator|(const BitArray& b1, const BitArray& b2) {
    BitArray result(b1);
    result |= b2;
    return result;
}


BitArray operator^(const BitArray& b1, const BitArray& b2) {
    BitArray result(b1);
    result ^= b2;
    return result;
}


