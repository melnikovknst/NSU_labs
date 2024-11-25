#include "bit_array.hpp"
#include "gtest/gtest.h"


TEST(BitArrayConstructorsTest, DefaultConstructor) {
    BitArray bitArray;
    
    EXPECT_EQ(bitArray.size(), 0);
    EXPECT_TRUE(bitArray.empty());
}


TEST(BitArrayConstructorsTest, ConstructorWithValue) {
    BitArray bitArray(10, 19);
    
    EXPECT_EQ(bitArray.size(), 10);
    EXPECT_EQ(bitArray.to_string(), "0000010011");
}


TEST(BitArrayConstructorsTest, CopyConstructor) {
    BitArray bitArray(64, 1);
    BitArray bitArray_copy(bitArray);
    
    ASSERT_EQ(bitArray.size(), bitArray_copy.size());
    for (int i = 0; i < bitArray.size(); i++)
        EXPECT_EQ(bitArray[i], bitArray_copy[i]);
}


TEST(IteratorTest, Iterator) {
    BitArray bitArray(64, 1);
    int count = 0;
    for (auto it = bitArray.begin(); it != bitArray.end(); ++it) {
        if (count == 0)
            ASSERT_EQ(*it, true);
        else
            ASSERT_EQ(*it, false);
        ++count;
    }
    
    ASSERT_EQ(count, 64);
}


TEST(IteratorTest, EqualityInequality) {
    BitArray bitArray(64, 1);
    
    auto it1 = bitArray.begin();
    auto it2 = bitArray.end();
    auto it3 = bitArray.begin();
    
    ++it1;
    ++it3;
    
    ASSERT_NE(it1, it2);
    ASSERT_EQ(it1, it3);
}


TEST(BitArrayAssignmentOperatorTest, SizeAndEquality) {
    BitArray bitArray1(64, 1);
    BitArray bitArray2;
    
    bitArray2 = bitArray1;
    
    ASSERT_EQ(bitArray1.size(), bitArray2.size());
    for (int i = 0; i < bitArray1.size(); i++)
        EXPECT_EQ(bitArray1[i], bitArray2[i]);
}


TEST(BitArrayResizeTest, IncreasingSizeFalse) {
    BitArray bitArray(64, 1);
    
    bitArray.resize(128, false);

    ASSERT_EQ(bitArray.size(), 128);
    ASSERT_EQ(bitArray[0], true);
    for (int i = 1; i < 128; ++i)
        ASSERT_EQ(bitArray[i], false);
}


TEST(BitArrayResizeTest, IncreasingSizeTrue) {
    BitArray bitArray(65, 1);
    
    bitArray.resize(127, true);

    ASSERT_EQ(bitArray.size(), 127);
    ASSERT_EQ(bitArray[0], true);
    for (int i = 1; i < 65; ++i)
        ASSERT_EQ(bitArray[i], false);
    
    for (int i = 65; i < 127; ++i)
        ASSERT_EQ(bitArray[i], true);
}


TEST(BitArrayResizeTest, DecreasingSize) {
    BitArray bitArray(64, 1);
    
    bitArray.resize(40, true);

    ASSERT_EQ(bitArray.size(), 40);
    ASSERT_EQ(bitArray[0], true);
    for (int i = 1; i < 40; ++i)
        ASSERT_EQ(bitArray[i], false);
}


TEST(BitArrayTest, Clear) {
    BitArray bitArray(64, 5436);
    
    bitArray.clear();
    
    ASSERT_EQ(bitArray.size(), 0);
    ASSERT_TRUE(bitArray.empty());
}


TEST(BitArrayTest, PushBack) {
    BitArray ba(64, 3245);
    
    ba.push_back(true);
    
    ASSERT_EQ(ba.size(), 65);
    ASSERT_EQ(ba[64], true);
}


TEST(BitArrayTest, BitwiseAndAssignment) {
    BitArray bitArray1(64, ULONG_MAX);
    BitArray bitArray2(64, 0);
    
    bitArray1 &= bitArray2;
    
    ASSERT_EQ(bitArray1.count(), 0);
    ASSERT_THROW(bitArray1 &= BitArray(128), std::invalid_argument);
}


TEST(BitArrayTest, BitwiseOrAssignment) {
    BitArray bitArray1(63, 0);
    bitArray1.push_back(true);
    BitArray bitArray2(64, 1);
    
    bitArray1 |= bitArray2;
    
    ASSERT_EQ(bitArray1[0], true);
    for (int i = 1; i < 63; ++i)
            ASSERT_EQ(bitArray1[i], false);
    
    ASSERT_EQ(bitArray1[63], true);
    ASSERT_THROW(bitArray1 &= BitArray(6), std::invalid_argument);
}


TEST(BitArrayTest, BitwiseXorAssignment) {
    BitArray bitArray1(7, 5);   //  0000101
    BitArray bitArray2(7, 76);  //  1001100
    
    bitArray1 ^= bitArray2;
    
    ASSERT_EQ(bitArray1.to_string(), "1001001");
    ASSERT_THROW(bitArray1 ^= BitArray(72), std::invalid_argument);
}


TEST(BitArrayLeftShiftAssignmentTest, Correctness) {
    BitArray bitArray(3, 5);
    
    bitArray <<= 1;
    
    EXPECT_EQ(bitArray.size(), 3);
    EXPECT_EQ(bitArray.count(), 1);
    EXPECT_TRUE(bitArray[1]);
}


TEST(BitArrayLeftShiftAssignmentTest, Overflow) {
    BitArray ba(65, 1);
    
    ba <<= 65;
    
    ASSERT_EQ(ba.count(), 0);
}


TEST(BitArrayRightShiftAssignmentTest, Correctness) {
    BitArray bitArray(7, 5);  //  ...0000101
    
    bitArray >>= 1;
    
    EXPECT_EQ(bitArray.size(), 7);
    EXPECT_EQ(bitArray.count(), 1);
    EXPECT_TRUE(bitArray[1]);
}


TEST(BitArrayRightShiftAssignmentTest, Overflow) {
    BitArray ba(5, 1);
    
    ba >>= 1;
    
    ASSERT_EQ(ba.count(), 0);
}


TEST(BitArrayTest, LeftShift) {
    BitArray bitArray(64, 1);
    
    BitArray shifted = bitArray << 1;
    
    EXPECT_EQ(bitArray.size(), 64);
    EXPECT_EQ(shifted.count(), 1);
    EXPECT_TRUE(shifted[1]);
}


TEST(BitArrayTest, RightShift) {
    BitArray bitArray(64, 1);
    
    BitArray shifted = bitArray >> 1;
    
    EXPECT_EQ(bitArray.size(), 64);
    EXPECT_EQ(shifted.count(), 0);
}


TEST(BitArrayTest, Swap) {
    BitArray bitArray1(10, 5);  //  0000000101
    BitArray bitArray2(7, 7);   //  0000111
    
    bitArray1.swap(bitArray2);

    EXPECT_EQ(bitArray1.count(), 3);
    EXPECT_EQ(bitArray2.count(), 2);
    EXPECT_EQ(bitArray1.size(), 7);
    EXPECT_EQ(bitArray2.size(), 10);
}


TEST(BitArrayTest, SetBitTest) {
    BitArray bitArray(3);
    
    bitArray.set(0, true);
    bitArray.set(1, false);
    
    EXPECT_EQ(bitArray.to_string(), "001");
    EXPECT_THROW(bitArray.set(100), std::out_of_range);
}


TEST(BitArrayTest, ResetBitTest) {
    BitArray bitArray(3, 4);
    
    bitArray.reset(0);
    bitArray.reset(1);
    
    EXPECT_EQ(bitArray.to_string(), "100");
    EXPECT_THROW(bitArray.reset(100), std::out_of_range);
}


TEST(BitArrayTest, AnyTest) {
    BitArray bitArray(45, 1456);
    
    EXPECT_TRUE(bitArray.any());
    bitArray.reset();
    EXPECT_FALSE(bitArray.any());
}


TEST(BitArrayTest, NoneTest) {
    BitArray bitArray(45, 1456);
    
    EXPECT_FALSE(bitArray.none());
    bitArray.reset();
    EXPECT_TRUE(bitArray.none());
}


TEST(BitArrayTest, BitwiseNot) {
    BitArray bitArray(45, 1456);
    
    BitArray not_bitArray = ~bitArray;
    
    ASSERT_EQ(not_bitArray.count(), bitArray.size() - bitArray.count());
}


TEST(BitArrayTest, Count) {
    BitArray bitArray(5, 7);
    
    ASSERT_EQ(bitArray.count(), 3);
}


TEST(BitArrayTest, AccessSpecificBit) {
    BitArray bitArray(10, 1);
    
    ASSERT_TRUE(bitArray[0]);
    ASSERT_FALSE(bitArray[9]);
    ASSERT_THROW(bitArray[11], std::out_of_range);
}


TEST(BitArrayTest, Size) {
    BitArray bitArray(646);
    
    ASSERT_EQ(bitArray.size(), 646);
}


TEST(BitArrayTest, Empty) {
    BitArray bitArray;
    
    ASSERT_TRUE(bitArray.empty());
}


TEST(BitArrayTest, ToString) {
    BitArray bitArray(7, 76);
    
    ASSERT_EQ(bitArray.to_string(), "1001100");
}


TEST(BitArrayTest, EqualityOperator) {
    BitArray bitArray1(7, 76);
    BitArray bitArray2(7, 76);
    
    ASSERT_TRUE(bitArray1 == bitArray2);
}


TEST(BitArrayTest, InequalityOperator) {
    BitArray bitArray1(7, 76);
    BitArray bitArray2(7, 75);
    
    ASSERT_TRUE(bitArray1 != bitArray2);
}


TEST(BitArrayTest, BitwiseAnd) {
    BitArray bitArray1(7, 5);   //  0000101
    BitArray bitArray2(7, 76);  //  1001100
    
    BitArray result = bitArray1 & bitArray2;
    
    ASSERT_EQ(result.to_string(), "0000100");
}


TEST(BitArrayTest, BitwiseOr) {
    BitArray bitArray1(7, 5);   //  0000101
    BitArray bitArray2(7, 76);  //  1001100
    
    BitArray result = bitArray1 | bitArray2;
    
    ASSERT_EQ(result.to_string(), "1001101");
}


TEST(BitArrayTest, proxyClass) {
    const int n = 65;
    BitArray bitArray(n);

    for (auto &v : bitArray)
        v = 1;

    ASSERT_EQ(bitArray.count(), n);
    
}
