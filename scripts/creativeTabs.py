#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Парсер креативных вкладок для HBM мода
Создает файлы для недостающих вкладок из оригинала 1.7.10
"""

import re
import os
from collections import defaultdict

def parse_existing_tabs(file_path):
    """Парсит существующие вкладки из HbmCreativeTabs.java"""
    # Пробуем разные кодировки
    encodings = ['utf-8', 'cp1251', 'latin-1', 'windows-1251']
    content = None
    
    for encoding in encodings:
        try:
            with open(file_path, 'r', encoding=encoding) as f:
                content = f.read()
                print(f"Файл прочитан с кодировкой: {encoding}")
                break
        except UnicodeDecodeError:
            continue
    
    if content is None:
        # Пробуем бинарное чтение
        with open(file_path, 'rb') as f:
            content_bytes = f.read()
            # Пробуем угадать кодировку
            for encoding in encodings:
                try:
                    content = content_bytes.decode(encoding)
                    print(f"Файл прочитан как {encoding} из байтов")
                    break
                except UnicodeDecodeError:
                    continue
    
    if content is None:
        print(f"Не удалось прочитать файл {file_path}")
        return set()
    
    existing_tabs = set()
    # Ищем все вкладки
    pattern1 = r'HBM_(\w+)_TAB\s*=\s*CREATIVE_MODE_TABS\.register\("hbm_(\w+)_tab"'
    pattern2 = r'HBM_(\w+)\s*=\s*CREATIVE_MODE_TABS\.register\("hbm_(\w+)"'
    
    for pattern in [pattern1, pattern2]:
        matches = re.findall(pattern, content)
        for match in matches:
            existing_tabs.add(match[0].upper())
    
    return existing_tabs

def parse_existing_items(file_path):
    """Парсит существующие предметы из HbmCreativeTabs.java"""
    # Пробуем разные кодировки
    encodings = ['utf-8', 'cp1251', 'latin-1', 'windows-1251']
    content = None
    
    for encoding in encodings:
        try:
            with open(file_path, 'r', encoding=encoding) as f:
                content = f.read()
                break
        except UnicodeDecodeError:
            continue
    
    if content is None:
        with open(file_path, 'rb') as f:
            content_bytes = f.read()
            for encoding in encodings:
                try:
                    content = content_bytes.decode(encoding)
                    break
                except UnicodeDecodeError:
                    continue
    
    if content is None:
        return set()
    
    # Ищем ModItems.ITEM.get()
    pattern1 = r'ModItems\.(\w+)\.get\(\)'
    # Ищем new ItemStack(ModItems.ITEM.get())
    pattern2 = r'new ItemStack\(ModItems\.(\w+)\.get\(\)'
    
    items = set()
    for pattern in [pattern1, pattern2]:
        matches = re.findall(pattern, content)
        items.update(matches)
    
    return items

def parse_original_mod_items():
    """Парсит предметы из оригинального мода 1.7.10"""
    # Более полный список предметов из оригинала
    original_items_by_tab = {
        'block': ['key', 'padlock', 'padlock_reinforced', 'padlock_rusty', 'padlock_unbreakable'],
        'parts': ['mech_key', 'pin', 'key_kit', 'key_fake'],
        'machine': ['assembly_template', 'chemistry_template', 'chemistry_icon'],
        'control': ['key_red', 'key_red_cracked'],
        'weapon': ['gun_revolver', 'gun_kit_1', 'gun_kit_2', 'ammo_357', 'ammo_44', 'ammo_50', 'ammo_9'],
        'nuke': ['nuke_mk4', 'nuke_mk5', 'nuke_schrabidium', 'nuke_fleija'],
        'missile': ['missile_nuclear', 'missile_thermo', 'missile_doomsday'],
        'consumable': ['key', 'key_kit', 'key_fake', 'pin', 'padlock_rusty', 'padlock', 'padlock_reinforced'],
        'template': ['blueprints', 'blueprint_folder', 'template_folder', 'assembly_template', 'chemistry_template']
    }
    
    # Иконки для каждой вкладки из оригинала
    tab_icons = {
        'block': 'BLOCK_ALUMINIUM',
        'parts': 'PLATE_STEEL',
        'machine': 'MACHINE_PRESS_ITEM',
        'control': 'REDSTONE_TORCH',
        'weapon': 'GUN_REVOLVER',
        'nuke': 'NUKE_MK4',
        'missile': 'MISSILE_NUCLEAR',
        'consumable': 'KEY',
        'template': 'BLUEPRINTS'
    }
    
    return original_items_by_tab, tab_icons

def get_missing_tabs(existing_tabs):
    """Возвращает список недостающих вкладок"""
    required_tabs = [
        ("BLOCK", "block"),
        ("PARTS", "parts"),
        ("MACHINE", "machine"),
        ("CONTROL", "control"),
        ("WEAPON", "weapon"),
        ("NUKE", "nuke"),
        ("MISSILE", "missile"),
        ("CONSUMABLE", "consumable"),
        ("TEMPLATE", "template")
    ]
    
    missing_tabs = []
    for tab_name, tab_id in required_tabs:
        if tab_name not in existing_tabs:
            missing_tabs.append((tab_name, tab_id))
    
    return missing_tabs

def get_icon_for_tab(tab_id, items, existing_items, tab_icons):
    """Возвращает иконку для вкладки"""
    # 1. Пробуем взять иконку из оригинала
    if tab_id in tab_icons:
        icon = tab_icons[tab_id]
        # Проверяем, есть ли эта иконка в существующих предметах
        if icon in existing_items:
            return icon
    
    # 2. Ищем первую существующую иконку из списка предметов этой вкладки
    for item in items:
        if item in existing_items:
            return item
    
    # 3. Ищем любую существующую иконку из всех предметов
    for item in existing_items:
        if 'BLOCK' in item or 'ITEM' in item or 'INGOT' in item:
            return item
    
    # 4. Запасной вариант
    return "STONE"

def create_tab_file(tab_name, tab_id, items, tab_icons, existing_items, output_dir):
    """Создает файл для креативной вкладки"""
    class_name = tab_id.capitalize() + "Tab"
    
    # Получаем правильную иконку
    icon_item = get_icon_for_tab(tab_id, items, existing_items, tab_icons)
    
    content = f'''package com.hbm.creativetabs;

import com.hbm.items.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class {class_name} {{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, "hbm");

    public static final RegistryObject<CreativeModeTab> HBM_{tab_name}_TAB = CREATIVE_MODE_TABS.register("{tab_id}_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.{tab_id}_tab"))
                    .icon(() -> new ItemStack(ModItems.{icon_item}.get()))
                    .displayItems((parameters, output) -> {{
                        // Предметы для вкладки {tab_id}
'''

    # Добавляем предметы
    for item in items:
        if item in existing_items:
            content += f'                        output.accept(new ItemStack(ModItems.{item}.get()));\n'
        else:
            content += f'                        // output.accept(new ItemStack(ModItems.{item}.get())); // TODO: Добавить в 1.20.1\n'
    
    # Если вкладка пустая, добавляем заглушку
    if not items:
        content += f'                        // Добавьте предметы для этой вкладки\n'
        content += f'                        // output.accept(new ItemStack(ModItems.{icon_item}.get()));\n'
    
    content += '''                    })
                    .build());
}
'''
    
    # Сохраняем файл
    file_path = os.path.join(output_dir, f"{class_name}.java")
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    return file_path, icon_item

def update_main_tabs_file(original_file, missing_tabs, items_by_tab, tab_icons, existing_items, output_dir):
    """Обновляет основной файл с добавлением недостающих вкладок"""
    # Пробуем разные кодировки
    encodings = ['utf-8', 'cp1251', 'latin-1', 'windows-1251']
    content = None
    
    for encoding in encodings:
        try:
            with open(original_file, 'r', encoding=encoding) as f:
                content = f.read()
                print(f"Основной файл прочитан с кодировкой: {encoding}")
                break
        except UnicodeDecodeError:
            continue
    
    if content is None:
        with open(original_file, 'rb') as f:
            content_bytes = f.read()
            for encoding in encodings:
                try:
                    content = content_bytes.decode(encoding)
                    break
                except UnicodeDecodeError:
                    continue
    
    if content is None:
        print(f"Не удалось прочитать файл {original_file}")
        return None
    
    # Находим место для вставки новых вкладок
    # Ищем последнюю закрывающую скобку перед концом файла
    insert_pos = content.rfind('\n}')
    if insert_pos == -1:
        insert_pos = len(content) - 1
    
    # Готовим контент для новых вкладок
    new_tabs_content = '\n'
    
    for tab_name, tab_id in missing_tabs:
        items = items_by_tab.get(tab_id, [])
        icon_item = get_icon_for_tab(tab_id, items, existing_items, tab_icons)
        
        new_tabs_content += f'''
    // Вкладка для {tab_id}
    public static final RegistryObject<CreativeModeTab> HBM_{tab_name}_TAB = CREATIVE_MODE_TABS.register("{tab_id}_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.hbm.{tab_id}_tab"))
                    .icon(() -> new ItemStack(ModItems.{icon_item}.get()))
                    .displayItems((parameters, output) -> {{
                        // Предметы для {tab_id}
'''
        
        for item in items:
            if item in existing_items:
                new_tabs_content += f'                        output.accept(new ItemStack(ModItems.{item}.get()));\n'
            else:
                new_tabs_content += f'                        // output.accept(new ItemStack(ModItems.{item}.get())); // TODO: Добавить в 1.20.1\n'
        
        # Если вкладка пустая, добавляем заглушку
        if not items:
            new_tabs_content += f'                        // Добавьте предметы для этой вкладки\n'
            new_tabs_content += f'                        output.accept(new ItemStack(ModItems.{icon_item}.get()));\n'
        
        new_tabs_content += '''                    })
                    .build());
'''
    
    # Вставляем новые вкладки перед последней скобкой
    updated_content = content[:insert_pos] + new_tabs_content + content[insert_pos:]
    
    # Сохраняем обновленный файл
    output_file = os.path.join(output_dir, "HbmCreativeTabs.java")
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write(updated_content)
    
    return output_file

def generate_instructions(missing_tabs, items_by_tab, existing_items, tab_icons, output_dir):
    """Генерирует файл с инструкциями"""
    instructions = """ИНСТРУКЦИЯ ДЛЯ ДОБАВЛЕНИЯ КРЕАТИВНЫХ ВКЛАДОК
================================================

Найдены следующие недостающие вкладки из оригинала 1.7.10:
"""

    for tab_name, tab_id in missing_tabs:
        items = items_by_tab.get(tab_id, [])
        icon_item = tab_icons.get(tab_id, "STONE")
        has_icon = icon_item in existing_items
        
        instructions += f"\n{tab_name}_TAB ({tab_id}_tab):"
        instructions += f"\n  Иконка: {icon_item} {'(есть в моде)' if has_icon else '(ОТСУТСТВУЕТ - нужно добавить!)'}"
        
        if items:
            instructions += f"\n  Предметы:"
            for item in items:
                if item in existing_items:
                    instructions += f"\n    ? {item}"
                else:
                    instructions += f"\n    ? {item} (отсутствует в 1.20.1)"
        else:
            instructions += "\n  (нет предметов в оригинальном моде)"
        instructions += "\n"
    
    instructions += """


ПОРЯДОК ДЕЙСТВИЙ:
================

1. СОЗДАННЫЕ ФАЙЛЫ:
   В папке 'creative_tabs_output' созданы файлы для каждой недостающей вкладки:
"""
    
    for tab_name, tab_id in missing_tabs:
        class_name = tab_id.capitalize() + "Tab"
        instructions += f"   - {class_name}.java\n"
    
    instructions += """

2. ИКОНКИ ВКЛАДОК:
   Иконки для вкладок взяты из оригинала 1.7.10. 
   ВАЖНО: Проверьте, что иконки существуют в вашем моде.
   Если иконка отсутствует (помечена как ОТСУТСТВУЕТ), вам нужно:
   - Либо добавить этот предмет
   - Либо заменить иконку на существующий предмет в созданном файле

3. ПРЕДМЕТЫ В КАЖДОЙ ВКЛАДКЕ:
   Предметы, которые уже есть в вашем моде 1.20.1, добавлены без комментариев.
   Предметы, которых нет, закомментированы с пометкой TODO.

4. ИНТЕГРАЦИЯ:
   а) Скопируйте созданные файлы в пакет com.hbm.creativetabs
   б) Или используйте обновленный HbmCreativeTabs.java из папки output
   в) Убедитесь, что все предметы из ModItems существуют
   г) Проверьте, что иконки для вкладок доступны

5. ДОБАВЛЕНИЕ НОВЫХ ПРЕДМЕТОВ:
   Для предметов, отмеченных как отсутствующие (TODO), вам нужно:
   - Добавить регистрацию в ModItems.java
   - Создать текстуры
   - Реализовать логику (если требуется)
   - Раскомментировать строки во вкладках

6. ПРИМЕРНЫЕ ПРЕДМЕТЫ ДЛЯ КАЖДОЙ ВКЛАДКИ:
   Если вы не знаете, какие предметы добавить в каждую вкладку:
   
   BLOCK_TAB:      Декоративные блоки, строительные блоки
   PARTS_TAB:      Детали машин, механические части
   MACHINE_TAB:    Целые машины, станки
   CONTROL_TAB:    Элементы управления, кнопки, переключатели
   WEAPON_TAB:     Оружие, боеприпасы
   NUKE_TAB:       Ядерные устройства, бомбы
   MISSILE_TAB:    Ракеты, пусковые установки
   CONSUMABLE_TAB: Расходные материалы, ключи, замки
   TEMPLATE_TAB:   Шаблоны, чертежи, схемы

7. ПРОВЕРКА:
   После добавления всех предметов проверьте работу в игре:
   - Все вкладки должны отображаться в креативном меню
   - Иконки должны быть корректными
   - Предметы должны быть кликабельны

ПРИМЕЧАНИЕ:
===========
Некоторые предметы из оригинала 1.7.10 могут не понадобиться в 1.20.1 или
могут быть заменены на аналогичные. Решайте на свое усмотрение, какие предметы
действительно нужно добавлять.
"""
    
    instructions_file = os.path.join(output_dir, "INSTRUCTIONS.txt")
    with open(instructions_file, 'w', encoding='utf-8') as f:
        f.write(instructions)
    
    return instructions_file

def main():
    print("Парсер креативных вкладок для HBM мода")
    print("=" * 60)
    print("Анализ существующего HbmCreativeTabs.java...")
    
    # Пути к файлам
    current_dir = os.path.dirname(os.path.abspath(__file__))
    hbm_tabs_file = os.path.join(current_dir, "HbmCreativeTabs.java")
    
    if not os.path.exists(hbm_tabs_file):
        print("Ошибка: файл HbmCreativeTabs.java не найден в текущей директории!")
        print(f"Искал по пути: {hbm_tabs_file}")
        print("\nУбедитесь, что файл находится в той же папке, что и этот скрипт.")
        return
    
    # Парсим существующие данные
    existing_tabs = parse_existing_tabs(hbm_tabs_file)
    existing_items = parse_existing_items(hbm_tabs_file)
    
    # Парсим оригинальные данные 1.7.10
    items_by_tab, tab_icons = parse_original_mod_items()
    
    # Находим недостающие вкладки
    missing_tabs = get_missing_tabs(existing_tabs)
    
    # Создаем папку для выходных файлов
    output_dir = os.path.join(current_dir, "creative_tabs_output")
    os.makedirs(output_dir, exist_ok=True)
    
    print(f"\nСтатистика:")
    print(f"  Найдено существующих вкладок: {len(existing_tabs)}")
    print(f"  Найдено существующих предметов: {len(existing_items)}")
    print(f"  Найдено недостающих вкладок: {len(missing_tabs)}")
    
    if not missing_tabs:
        print("\nВсе вкладки уже существуют!")
        return
    
    print(f"\nСоздание файлов для {len(missing_tabs)} недостающих вкладок...")
    
    # Создаем отдельные файлы для каждой вкладки
    created_files = []
    for tab_name, tab_id in missing_tabs:
        items = items_by_tab.get(tab_id, [])
        file_path, icon_item = create_tab_file(tab_name, tab_id, items, tab_icons, existing_items, output_dir)
        created_files.append((file_path, icon_item))
        print(f"  Создан: {os.path.basename(file_path)} (иконка: {icon_item})")
    
    # Обновляем основной файл
    print("\nОбновление основного файла HbmCreativeTabs.java...")
    updated_file = update_main_tabs_file(hbm_tabs_file, missing_tabs, items_by_tab, tab_icons, existing_items, output_dir)
    if updated_file:
        print(f"  Обновлен: {os.path.basename(updated_file)}")
    
    # Генерируем инструкции
    print("\nСоздание инструкций...")
    instructions_file = generate_instructions(missing_tabs, items_by_tab, existing_items, tab_icons, output_dir)
    print(f"  Создан: {os.path.basename(instructions_file)}")
    
    # Сводка
    print("\n" + "="*60)
    print("СВОДКА:")
    print("="*60)
    print(f"Создано файлов вкладок: {len(created_files)}")
    
    for (tab_name, tab_id), (file_path, icon_item) in zip(missing_tabs, created_files):
        items = items_by_tab.get(tab_id, [])
        existing_in_tab = [item for item in items if item in existing_items]
        missing_in_tab = [item for item in items if item not in existing_items]
        
        print(f"\n{tab_name}_TAB:")
        print(f"  Иконка: {icon_item}")
        print(f"  Всего предметов: {len(items)}")
        print(f"  Есть в моде: {len(existing_in_tab)}")
        print(f"  Отсутствует: {len(missing_in_tab)}")
        
        if missing_in_tab:
            print(f"  Отсутствующие предметы:")
            for item in missing_in_tab:
                print(f"    - {item}")
    
    print(f"\nВсе файлы сохранены в папке: {output_dir}")
    print("\nВАЖНО: Проверьте файл INSTRUCTIONS.txt для получения полных инструкций!")
    print("\nСледующие шаги:")
    print("1. Проверьте иконки в созданных файлах")
    print("2. Добавьте недостающие предметы в ModItems.java")
    print("3. Раскомментируйте предметы во вкладках")
    print("4. Протестируйте в игре")

if __name__ == "__main__":
    main()