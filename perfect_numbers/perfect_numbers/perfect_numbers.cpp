#include <iostream>

// Prototypes
void display_numbers(const int);
int get_amount(void);


void display_numbers(const int amount) {
    int count_of_numbers{ 1 };
    int a{ 2 }, b{ 0 };
    long sum{ 1L }, num{ 2L };

    while (count_of_numbers <= amount)
    {
        sum = 1;
        a = 2;
        b = (int)(num / a);

        while (b > a)
        {
            if (num % a == 0)
            {
                sum = sum + a + b;
            }
            a = a + 1; b = (int)(num / a);

        }
        if (b == a && num % a == 0)
        {
            sum = sum + a;
        }
        if (sum == num)
        {
            std::cout << num << std::endl;
            count_of_numbers++;
        }
        num++;
    }
}

int get_amount() {
    int amount{ 0 };

    std::cout << "Enter how many first perfect numbers to generate: ";
    std::cin >> amount;

    return amount;
}

int main()
{
    int amount = get_amount();

    std::cout << "----------------------------------" << std::endl;
    std::cout << "\t Perfect numbers \t" << std::endl;
    std::cout << "----------------------------------" << std::endl;

    display_numbers(amount);

    std::cout << std::endl;

    system("pause");
    return 0;

}


