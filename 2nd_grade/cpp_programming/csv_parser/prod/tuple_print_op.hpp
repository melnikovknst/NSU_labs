#ifndef tuple_print_op_hpp
#define tuple_print_op_hpp

#include <iostream>
#include <tuple>
#include <string>


template<std::size_t Index, typename Tuple, typename Ch, typename Tr>
void print_tuple(std::basic_ostream<Ch, Tr>& os, const Tuple& t) {
    if constexpr (Index < std::tuple_size<Tuple>::value) {
        if (Index > 0) os << " | ";
        os << std::get<Index>(t);
        print_tuple<Index + 1>(os, t);
    }
}


template<typename Ch, typename Tr, typename... Args>
auto operator<<(std::basic_ostream<Ch, Tr>& os, const std::tuple<Args...>& t) -> std::basic_ostream<Ch, Tr>& {
    print_tuple<0>(os, t);
    return os;
}


#endif /* tuple_print_op_hpp */
     

